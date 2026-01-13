import http from 'k6/http';
import { sleep } from 'k6';

export const options = {
  stages: [
    { duration: '30s', target: 50 },
    { duration: '1m', target: 100 },
    { duration: '30s', target: 0 }
  ],
};

export default function () {
  // Hot set = first 20% of IDs
  const hotId = Math.floor(Math.random() * 200000) + 1;

  http.get(`http://localhost:8082/api/v1/products/${hotId}`);
  sleep(0.1);
}
