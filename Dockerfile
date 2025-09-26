FROM node:20-alpine AS build

WORKDIR /app

COPY package*.json ./
RUN npm ci
COPY . .

RUN npm run build

FROM nginx:alpine

COPY --from=build /app/dist /usr/share/nginx/html

RUN mkdir -p /var/cache/nginx /var/run /var/log/nginx \
  && chgrp -R 0 /var/cache/nginx /var/run /var/log/nginx /usr/share/nginx/html \
  && chmod -R g+rwX /var/cache/nginx /var/run /var/log/nginx /usr/share/nginx/html

EXPOSE 8080
CMD ["nginx", "-g", "daemon off;"]
