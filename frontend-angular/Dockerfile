# Dockerfile — frontend-angular (Puerto 80 dentro del contenedor)
FROM node:22-alpine AS build
WORKDIR /app

# Copiar dependencias y compilar
COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npm run build --configuration=production

# Servidor Nginx
FROM nginx:alpine
# Copiar configuración personalizada para Angular router
COPY nginx.conf /etc/nginx/conf.d/default.conf
# Copiar los archivos compilados de Angular
COPY --from=build /app/dist/app-ferreteria/browser /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
