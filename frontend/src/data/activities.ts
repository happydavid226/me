import type { Activity, Education } from './types'

export const education: Education[] = [
  { title: 'Software engineering studies', place: 'Rwanda Coding Academy', period: 'Current', kind: 'Education', description: 'Studying software engineering.' },
]
export const activities: Activity[] = []

// Verify role titles, dates, and active status before moving activities onto the public page.
export const activitiesToVerify: Activity[] = [
  { title: 'The Gym Rwanda', kind: 'Teaching & mentoring', period: 'Dates and active status to confirm', description: 'Reported involvement in teaching and mentoring students in technology. Confirm role, dates, and current activity.', status: 'Verify' },
  { title: 'RCA Business Club', kind: 'School activity', period: 'Dates and role to confirm', description: 'A secretary role has been reported. Confirm title, dates, and current activity.', status: 'Verify' },
  { title: 'Competitive programming', kind: 'Independent practice', period: 'Ongoing · confirm current goal', description: 'Algorithmic problem-solving and olympiad preparation. Add a current goal and dates when confirmed.', status: 'Verify' },
]
