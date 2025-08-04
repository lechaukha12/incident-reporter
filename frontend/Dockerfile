FROM node:16-alpine as build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .

# Expose port and run development server with the updated settings
EXPOSE 4200
CMD ["npm", "run", "start:dev", "--", "--host", "0.0.0.0", "--disable-host-check", "--port", "4200"]
