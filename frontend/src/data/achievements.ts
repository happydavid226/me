import type { Achievement } from './types'

// Review each verification note against an official result before publishing. URLs stay absent until verified.
export const achievements: Achievement[] = []

// Private editorial queue: confirm each item against official result records before moving it into achievements.
export const achievementsToVerify: Achievement[] = [
  { title: 'International Olympiad in Informatics', category: 'International programming competition', date: '2025 · verify official year and wording', description: 'Reported participation as a member of Team Rwanda; reported score of 32.76 and ranking of 319. Confirm the official standings and rank context before publication.', verification: 'Verify event year, team participation, score and ranking against the official standings.' },
  { title: 'International Olympiad in Informatics', category: 'International programming competition', date: '2026 · verify official year and wording', description: 'A score of 84.32 out of 600 has been reported. Confirm the official competition and standings before publication.', verification: 'Verify event year, score and official result before publication.' },
  { title: 'Pan-African Informatics Olympiad', category: 'Regional programming competition', date: 'Year to verify', description: 'A gold medal and score of 412/800 have been reported. They are kept separate from published claims until the year and official award can be confirmed.', verification: 'Verify the year and official award against the standings before publishing medal and score together.' },
]
