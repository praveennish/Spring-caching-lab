import http from 'k6/http';
import { sleep } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8081';

export const options = {
  stages: [
    { duration: '10s', target: 0 },
    { duration: '10s', target: 200 },   // 👈 spike
    { duration: '20s', target: 200 },
    { duration: '10s', target: 0 }
  ],
};

export default function () {
  const hotId = 1;   // 👈 force same key
  http.get(`${BASE_URL}/api/v1/products/${hotId}`);
}