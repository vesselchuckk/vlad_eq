pin-images:
	@echo "eclipse-temurin:17-jdk-jammy->"
	@docker pull -q eclipse-temurin:17-jdk-jammy >/dev/null
	@docker inspect eclipse-temurin:17-jdk-jammy --format '{{index .RepoDigests 0}}'
	@echo "alpine:3.22 ->"
	@docker pull -q alpine:3.22 >/dev/null
	@docker inspect alpine:3.22 --format '{{index .RepoDigests 0}}'