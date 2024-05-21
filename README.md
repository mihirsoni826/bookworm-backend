<img alt="Banner" src="readme_images/Bookworm banner.png" width="100%"/>

This Spring service serves as the Backend API for
the [Bookworm React App](https://github.com/mihirsoni826/bookworm-frontend).

<hr>

## Run Locally

Clone the project

```bash
  git clone https://github.com/mihirsoni826/bookworm-backend
```

#### Environment Variables

To run this project, you will need to add the following environment variables to your run configurations.

`BOOKWORM_API_KEY` - API key for the [New York Times API](https://developer.nytimes.com/)<br>
`bookworm.dev-url=http://localhost:8080`<br>
`bookworm.prod-url=https://bookworm-backend-b18696582ebc.herokuapp.com`

#### Install all dependencies

```bash
mvn clean install
```

## API Reference

[Swagger](https://bookworm-backend-b18696582ebc.herokuapp.com/swagger-ui.html)