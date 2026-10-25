export interface ContactPayload { name: string; email: string; message: string; website: string }
export async function sendContact(payload: ContactPayload): Promise<string> {
  const response = await fetch('/api/contact', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) })
  const body = await response.json().catch(() => ({})) as { message?: string }
  if (!response.ok) throw new Error(body.message || 'Your message could not be sent. Please try again.')
  return body.message || 'Your message was accepted by the mail server.'
}
