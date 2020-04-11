FROM node:12

RUN mkdir -p /usr/src
WORKDIR /usr/src

COPY package.json .
COPY package-lock.json .

RUN npm install

COPY ./server.js .
COPY ./server ./server

ENTRYPOINT ["npm"]

CMD ["start"]
