# Kelteu Reverse Geocoding Service

Kelteu Reverse Geocoding Service is a lightweight, Java based, reverse geocoding / location service. 
It offers latitude/longitude and/or IP based reverse geocoding functionality.

The service is free to use and/or modify.   


To clone the project locally
```
git clone https://github.com/Kelteu/Kelteu-Reverse-Geocoding-Service.git
cd Kelteu-Reverse-Geocoding-Service
git config --local core.fileMode false
```

In order to build the project, you will need to have Java 21 installed on your system and JAVA_HOME environment variable defined.
To build the project run
```
./build.sh
```

Before you build make sure that the docker service is running:
```
systemctl start docker
```

To start docker service automatically on boot:
```
sudo systemctl enable docker
```

And you have the right permissions to use it
```
sudo chmod 666 /var/run/docker.sock
```

Before starting the containers for the very first time, you will have to create the kelteu-network
```
docker network create kelteu-network
```

To start service containers run
```
docker-compose up
```

To see the swagger console showcasing the available APIs:
```
http://localhost:82/swagger-ui/index.html
```


