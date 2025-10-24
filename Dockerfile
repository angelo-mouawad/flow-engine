FROM node:20-alpine AS build

WORKDIR /app

COPY package*.json ./
RUN npm ci
RUN echo "VITE_API_URL=https://back-end-team30-wpp-team-30.apps.okd.ucll.cloud/" > .env
COPY . .

RUN npm run build

FROM nginx:stable-alpine AS runner

# Remove default nginx static files and copy app build
RUN rm -rf /usr/share/nginx/html/*
COPY --from=build /app/dist /usr/share/nginx/html

# Write nginx.conf from Dockerfile (embedded configuration)
RUN cat > /etc/nginx/nginx.conf <<'EOF'
worker_processes  1;
# no 'user' directive so the image can run as arbitrary UID on OpenShift/OKD
error_log  /var/log/nginx/error.log warn;
pid        /var/run/nginx.pid;

events {
    worker_connections  1024;
}

http {
    include       /etc/nginx/mime.types;
    default_type  application/octet-stream;

    sendfile        on;
    keepalive_timeout  65;

    # use a runtime-writable client_temp path to avoid permission issues
    client_body_temp_path /tmp/nginx_client_temp;

    gzip on;

    server {
        listen       8080;
        listen       [::]:8080;
        server_name  _;
        root /usr/share/nginx/html;
        index index.html;

        # SPA fallback
        location / {
            try_files $uri $uri/ /index.html;
        }

        # cache static assets
        location ~* \.(?:css|js|jpg|jpeg|gif|png|svg|ico|woff2?)$ {
            try_files $uri =404;
            expires 30d;
            access_log off;
        }
    }
}
EOF

# Ensure OpenShift/OKD arbitrary UID can write to nginx runtime dirs and nginx config dirs
RUN mkdir -p /var/cache/nginx /var/run /var/log/nginx /etc/nginx/conf.d && \
    chown -R 0:0 /var/cache/nginx /var/run /var/log/nginx /usr/share/nginx/html /etc/nginx && \
    chmod -R g+rwX /var/cache/nginx /var/run /var/log/nginx /usr/share/nginx/html /etc/nginx

# Optional: add a simple nginx config for SPA history mode (uncomment if needed)
# COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 8080
CMD ["nginx", "-g", "daemon off;"]
