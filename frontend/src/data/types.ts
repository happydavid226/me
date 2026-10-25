export interface Profile { name: string; title: string; location: string; email: string; summary: string; github?: string; linkedin?: string; cv?: string }
export interface Achievement { title: string; category: string; date: string; description: string; link?: string; verification?: string }
export interface Project { name: string; status: 'Completed' | 'In progress' | 'Experimental' | 'Planned'; description: string; technologies: string[]; repository?: string; demo?: string }
export interface SkillGroup { category: string; items: { name: string; note?: string }[] }
export interface Activity { title: string; kind: string; period: string; description: string; status?: 'Current' | 'Verify' }
export interface Education { title: string; place: string; period: string; description: string; kind: string }
export interface CurrentFocus { icon: 'code' | 'layers' | 'sparkles'; title: string; description: string }
