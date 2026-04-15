/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        ink: '#0a0b0f',
        surface: '#111318',
        card: '#161a22',
        'border-theme': '#252b38',
        muted: '#6b7591',
        text: '#cdd5f0',
        white: '#f0f3ff',
        accent: '#5c6dff',
        glow: '#7c8dff',
        lime: '#b6f542',
        rose: '#ff4d6d',
        gold: '#f0b429',
      },
      fontFamily: {
        sans: ['DM Sans', 'sans-serif'],
        serif: ['Instrument Serif', 'serif'],
        syne: ['Syne', 'sans-serif'],
      },
      borderRadius: {
        talent: '14px',
      },
      animation: {
        'fade-up': 'fadeUp 0.9s ease both',
        'scroll-logos': 'scrollLogos 22s linear infinite',
        'pulse': 'pulse 2s ease-in-out infinite',
      },
      keyframes: {
        fadeUp: {
          '0%': { opacity: '0', transform: 'translateY(30px)' },
          '100%': { opacity: '1', transform: 'translateY(0)' },
        },
        scrollLogos: {
          '0%': { transform: 'translateX(0)' },
          '100%': { transform: 'translateX(-50%)' },
        },
        pulse: {
          '0%, 100%': { transform: 'scale(1)', opacity: '1' },
          '50%': { transform: 'scale(1.4)', opacity: '0.7' },
        },
      },
    },
  },
  plugins: [],
}

