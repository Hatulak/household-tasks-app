Docker image build:
mvnw spring-boot:build-image -Dspring.profiles.active=dev

Docker run:
docker run -e "SPRING_PROFILES_ACTIVE=dev" --tty --publish 8091:8091 backend:0.0.1-SNAPSHOT
