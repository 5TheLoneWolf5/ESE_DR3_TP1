"use client";
import { createTheme } from '@mui/material/styles';

const theme = createTheme({
  cssVariables: {
    colorSchemeSelector: 'data-toolpad-color-scheme',
  },
  colorSchemes: {
    light: {
      palette: {
        primary: {
          main: '#2563eb',
          light: '#60a5fa',
          dark: '#1d4ed8',
          contrastText: '#ffffff',
        },
        secondary: {
          main: '#059669',
          light: '#34d399',
          dark: '#047857',
          contrastText: '#ffffff',
        },
        text: {
          primary: '#0f172a',
          secondary: '#475569',
          disabled: '#94a3b8',
        },
        background: {
          default: '#f8fafc',
          paper: '#ffffff',
        },
        divider: '#e2e8f0',
        action: {
          hover: 'rgba(0, 0, 0, 0.04)',
          selected: 'rgba(37, 99, 235, 0.08)',
        },
      },
    },
    dark: {
      palette: {
        primary: {
          main: '#3b82f6',
          light: '#93c5fd',
          dark: '#1d4ed8',
          contrastText: '#ffffff',
        },
        secondary: {
          main: '#10b981',
          light: '#6ee7b7',
          dark: '#047857',
          contrastText: '#ffffff',
        },
        text: {
          primary: '#f8fafc',
          secondary: '#94a3b8',
          disabled: '#64748b',
        },
        background: {
          default: '#0b0f19',
          paper: '#1e293b',
        },
        divider: '#334155',
        action: {
          hover: 'rgba(255, 255, 255, 0.06)',
          selected: 'rgba(59, 130, 246, 0.16)',
        },
      },
    },
  },
  typography: {
    fontFamily: [
      'Inter',
      '-apple-system',
      'BlinkMacSystemFont',
      '"Segoe UI"',
      'Roboto',
      '"Helvetica Neue"',
      'Arial',
      'sans-serif',
    ].join(','),
    h4: {
      fontWeight: 800,
    },
    h5: {
      fontWeight: 700,
    },
    h6: {
      fontWeight: 700,
    },
    subtitle1: {
      fontWeight: 600,
    },
  },
  shape: {
    borderRadius: 12,
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: 10,
          textTransform: 'none',
          fontWeight: 600,
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          borderRadius: 14,
          backgroundImage: 'none',
        },
      },
    },
    MuiPaper: {
      styleOverrides: {
        root: {
          borderRadius: 12,
          backgroundImage: 'none',
        },
      },
    },
    MuiOutlinedInput: {
      styleOverrides: {
        root: {
          borderRadius: 10,
        },
      },
    },
    MuiInputLabel: {
      styleOverrides: {
        root: {
          fontWeight: 500,
        },
      },
    },
  },
});

export default theme;
