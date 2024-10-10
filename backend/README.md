# Backend Web Engineering Template Project
This is the template project for the BWENG course.

## Requirements
* Docker
    * [Get Docker](https://docs.docker.com/get-docker/)

## Container
* Spring Boot basic setup container
  * Port 8080
* MariaDB container
  * Port 3306
* MinIO container
  * Port 9000
  * Port 9001 (Dashboard)

## Component Diagram
![App Componenet Diagram](http://www.plantuml.com/plantuml/png/POxDIiL038NtUOfmz_SDHAwttRWGmJx1E1DhS9eCcTID-EwMbj8VTydv3dpdLZsOZqE6J1-EhcZSVpecDehEAW0XkXescKaSG3GHjXg_oF074ACEHML2UEcAiVHuLtLyAkKoytsZKN7JdCbEe2FxvaZr5BzHqSgknZFw1K1CmSDxg8GlmJYqzsF6ylmAKmzWsOiFr-lZthkTCzhCwx741_Fsh7Xr_oVBWXj96eVy1m00)

## Already installed dependecies
* springdoc-openapi
  * /api (API json)
  * /swagger.html (Swagger API UI)

## Setup
##
Build Docker container
```shell
docker compose build
```
Start Docker container (with build)
```shell
docker compose up
docker compose up --build
```
Stop Docker container
```shell
docker compose stop
```
Remove Docker container
```shell
docker compose down
```
