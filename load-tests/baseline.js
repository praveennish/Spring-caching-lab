import http from 'k6/http';
import { sleep } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8081';

export const options = {
  stages: [
    { duration: '30s', target: 20 },
    { duration: '1m', target: 50 },
    { duration: '30s', target: 0 }
  ],
};

export default function () {
  const hotId = Math.floor(Math.random() * 200000) + 1;
  http.get(`${BASE_URL}/api/v1/products/${hotId}`);
  sleep(0.1);
}
