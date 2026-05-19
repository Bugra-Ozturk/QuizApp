/** @type {import('tailwindcss').Config} */
export default {
  content: [
    './index.html',
    './src/**/*.{vue,js,ts}'
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          50:  '#f0f4ff',
          100: '#e0eaff',
          500: '#4f6ef7',
          600: '#3d5ce6',
          700: '#2e4bc4',
          800: '#1e34a0',
          900: '#0f1e7c',
        },
        surface: {
          50:  '#fafafa',
          100: '#f5f5f5',
          800: '#1a1a2e',
          900: '#0f0f1a',
        }
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif']
      }
    }
  },
  plugins: []
}
