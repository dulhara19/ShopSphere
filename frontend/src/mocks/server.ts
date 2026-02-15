/**
 * MSW Server Setup
 *
 * Initializes Mock Service Worker for Node.js environment (SSR/testing).
 */

import { setupServer } from 'msw/node';
import { handlers } from './handlers';

export const server = setupServer(...handlers);
