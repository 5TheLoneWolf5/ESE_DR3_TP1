FROM node:24-alpine AS build-stage

WORKDIR /app

COPY ./Front-end/package*.json ./
RUN npm install

COPY ./Front-end .
RUN npm run build

CMD ["sh", "-c", "npm run preview -- --host 0.0.0.0 --port $PORT"]
