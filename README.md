# Shigaraki 

Shigaraki is an API in Kotlin using the Framework Ktor, it communicate with Midoriya and an Elasticsearch instance. So, when you create a product with Midoriya, Shigaraki retrieve the request and update an Elasticsearch instance.

## Getting started

### Required

- [Elasticsearch](https://www.elastic.co/guide/en/elasticsearch/reference/current/getting-started.html)
- [Docker Desktop](https://hub.docker.com/)

### Setting up Shigaraki

- Set up the [Elasticsearch instance](https://www.elastic.co/guide/en/elasticsearch/reference/current/getting-started-install.html)
- Start Elasticsearch from the bin directory `cd elasticsearch-7.8.1/bin`
- Run `./elasticsearch`
- Clone or download the repository
- Build the project
- Run the application

## API / Endpoints / Main commands

You can try the API with the Postman collection `Shigaraki.postman_collection.json`

---

- **GET** `/product/search/{product}` - Search by product's name