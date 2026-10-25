import type { Project } from './types'

// Candidate projects are deliberately marked planned until their state and public details are confirmed.
export const projects: Project[] = []

// Private editorial queue: confirm existence, status, stack and public URLs before moving these into projects.
export const projectCandidates: Project[] = [
  { name: 'Typing leaderboard', status: 'Planned', description: 'A school leaderboard concept built around classmates’ Monkeytype personal-best results. Scope, data source, and implementation status need confirmation.', technologies: ['To confirm'] },
  { name: 'Skyglide', status: 'Planned', description: 'An airline-related backend project. The current implementation, feature set, and public repository need confirmation.', technologies: ['Java', 'Spring Boot · to confirm'] },
  { name: 'Rwanda Olympiad Foundation LMS', status: 'Planned', description: 'A learning management system concept for olympiad learning. Verify implementation status and intended users before publishing further details.', technologies: ['To confirm'] },
  { name: 'Nursery marks management', status: 'Planned', description: 'A marks-management application concept for a nursery school. Status and technologies need confirmation.', technologies: ['To confirm'] },
  { name: 'Nursery registration', status: 'Planned', description: 'A registration application concept for a nursery school. Status and technologies need confirmation.', technologies: ['To confirm'] },
]
