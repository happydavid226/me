package rw.happydavid.portfolio.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import rw.happydavid.portfolio.contact.ApiResponse;
import rw.happydavid.portfolio.contact.RateLimiter;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RequestLimitsFilter extends OncePerRequestFilter {
    private static final int MAX_CONTACT_BYTES = 12 * 1024;
    private final RateLimiter rateLimiter;
    public RequestLimitsFilter(RateLimiter rateLimiter) { this.rateLimiter = rateLimiter; }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !"POST".equalsIgnoreCase(request.getMethod()) || !"/api/contact".equals(request.getRequestURI());
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // The reverse proxy overwrites X-Real-IP; the backend is not published outside the Compose network.
        String address = request.getHeader("X-Real-IP");
        if (address == null || address.isBlank()) address = request.getRemoteAddr();
        if (!rateLimiter.allow(address == null ? "unknown" : address)) {
            response.setStatus(429); response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"message\":\"Too many messages. Please try again later.\"}"); return;
        }
        if (request.getContentLengthLong() > MAX_CONTACT_BYTES) {
            response.setStatus(413); response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"message\":\"Request is too large.\"}"); return;
        }
        byte[] body = request.getInputStream().readNBytes(MAX_CONTACT_BYTES + 1);
        if (body.length > MAX_CONTACT_BYTES) {
            response.setStatus(413); response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"message\":\"Request is too large.\"}"); return;
        }
        chain.doFilter(new ReplayableRequest(request, body), response);
    }
}
