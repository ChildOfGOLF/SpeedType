FROM nginx:alpine

COPY pages/index.html /usr/share/nginx/html/index.html
COPY pages/game.html /usr/share/nginx/html/game.html
COPY pages/login.html /usr/share/nginx/html/login.html
COPY pages/progress.html /usr/share/nginx/html/progress.html

COPY styles/ /usr/share/nginx/html/styles/
COPY scripts/ /usr/share/nginx/html/scripts/
COPY localization/ /usr/share/nginx/html/localization/
COPY admin/ /usr/share/nginx/html/admin/
EXPOSE 80
