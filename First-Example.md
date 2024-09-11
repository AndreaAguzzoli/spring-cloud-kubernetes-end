# First Example

## Service Discovery Kubernetes

In Kubernetes, the **integrated service discovery mechanism** is a fundamental component facilitating communication among various components or microservices **within a cluster**. Its significance lies in the dynamic nature of the Kubernetes environment, wherein containers and services can be continuously deployed, scaled, or relocated.

Kubernetes exposes an **Endpoint API** that allows users to interact with endpoint objects directly. Users can create, update, or delete endpoint objects using the Endpoint API. When a service is created or updated, Kubernetes automatically creates and manages the corresponding endpoint objects. This enables dynamic management of network addresses associated with a service as pods are scaled up, scaled down, or replaced within the cluster. This integration ensures that traffic to the service is properly routed and load-balanced across the pods that provide the service.

Kubernetes also offers a **Service API**, which enables users to define and manage services within the cluster. Services abstract away the details of individual pods by providing a single endpoint for accessing a set of pods that provide the same functionality. The Service API allows users to create, update, or delete services, as well as configure various aspects such as load balancing and session affinity. **Services** in Kubernetes provide a stable endpoint for accessing pods, making it easy for other components within the cluster to discover and communicate with them.

Kubernetes utilizes **DNS-based service discovery** as a mechanism for identifying services within the cluster. Each service defined in Kubernetes is assigned a DNS name, which can be used by other components within the cluster to discover and communicate with the service. Here's an overview of how DNS-based service discovery works in Kubernetes:  

  - **DNS Naming Convention**: Each service defined in Kubernetes is assigned a **DNS name** following a specific **naming convention**. The format typically includes the service name, namespace, and the domain suffix `.svc.cluster.local`. For example, if you have a service named `my-service` in the `default` namespace, its DNS name would be `my-service.default.svc.cluster.local`.  
  - **Service Resolution**: Kubernetes configures DNS servers within the cluster to resolve these DNS names to the IP addresses of the pods associated with the corresponding service. When a client within the cluster attempts to access a service using its DNS name, the DNS server resolves the name to one or more IP addresses of the pods providing that service.  
  - **Automatic Updates**: Kubernetes automatically updates DNS records when services are created, deleted, or modified. This ensures that DNS names remain up-to-date and accurately reflect the current state of the services within the cluster. As pods are scaled up or down, the DNS records are updated accordingly to include or remove the IP addresses of the pods.    
  - **Kube-proxy**: Though not an API in the traditional sense, kube-proxy is a component within Kubernetes responsible for implementing service discovery and load balancing. It maintains network rules on each node to forward traffic to the appropriate pods based on service endpoints. While users don't directly interact with kube-proxy through an API, its functionality is crucial for enabling service discovery within the cluster.  
  - **Service Discovery**: DNS-based service discovery simplifies the process of identifying services within the cluster. Instead of hardcoding IP addresses or endpoints in configuration files or code, components can use DNS names to dynamically discover and communicate with services. DNS-based service discovery also enables communication between services in different namespaces within the same cluster. Since DNS names include the namespace of the service, components can easily reference services across namespaces using their DNS names. When a service is created, Kubernetes automatically configures DNS records for that service, making it immediately accessible via its DNS name.  

Overall, the integrated discovery service in Kubernetes provides a reliable and scalable mechanism for enabling microservices within a cluster to dynamically communicate with each other, contributing to the resilience and elasticity of distributed applications.

## Example Schema 

The directory structure for our example is as follows:

```
spring-cloud-kubernetes-end/  
├── composite-service-end/  
│   ├── dockerfile  
│   ├── pom.xml  
│   └── src/  
│       └── main/  
│           ├── java/  
│           │   └── com/  
│           │       └── example/  
│           │           └── composite/  
│           │               ├── CompositeApplication.java  
│           │               ├── CompositeController.java  
│           │               ├── LocalDateTimeWithTimestamp.java  
│           │               ├── LocalDateWithTimestamp.java  
│           │               └── LocalTimeWithTimestamp.java  
│           └── resources/  
│               └── application.yaml  
├── time-service-end/  
│   ├── dockerfile  
│   ├── pom.xml  
│   └── src/  
│       └── main/  
│           ├── java/  
│           │   └── com/  
│           │       └── example/  
│           │           └── time/  
│           │               ├── TimeServiceApplication.java  
│           │               └── TimeController.java  
│           └── resources/  
├── date-service-end/  
│   ├── dockerfile  
│   ├── pom.xml  
│   └── src/  
│       └── main/  
│           ├── java/  
│           │   └── com/  
│           │       └── example/  
│           │           └── date/  
│           │               ├── DateServiceApplication.java  
│           │               └── DateController.java  
│           └── resources/  
├── kube/  
│   ├── composite-service.yaml  
│   ├── date-service.yaml  
│   └── time-service.yaml  
└── pom.xml  
```

### composite-service-end

#### LocalTimeWithTimeStamp

```java
package com.baeldung.composite;

import java.time.LocalTime;

public record LocalTimeWithTimestamp(LocalTime localTime, LocalTime timestamp) {
}
```

The use of `record` in Java is a modern and concise way to define classes, especially for simple data holders.

The same applies to the `LocalDateWithTimestamp` class, which has the identical structure but contains `localDate` and `timestamp`.

The class structure is the same in `LocalDateWithTimestamp`, but it includes two objects corresponding to the two previous types, namely a `LocalDateWithTimestamp` object and a `LocalTimeWithTimestamp` object.

#### CompositeController

We can see that this class represents a Spring Boot **Controller** that makes calls to external services using `WebClient`, one to obtain the time and another to obtain the date, and then combines the responses into a `LocalDateTimeWithTimestamp` object. In a Spring Boot application, a **controller** is a class that handles HTTP requests. They receive requests, process the data (often delegating the work to other services), and return a response. In our specific case, the `dateTime` method is mapped to the `/datetime` endpoint using `@GetMapping`.

- **import**:

```java
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalTime;
```

In the **imports**, we can see those necessary for **Logging** (`LoggerFactory`, `Logger`), those for defining a **REST controller** (`GetMapping`, `RestController`), those for handling HTTP requests (`WebClient` and `Mono`), and the date and time classes (`LocalDate`, `LocalTime`).

- **Declaration**:

```java
@RestController
public class CompositeController {
  ...
}
```

`Spring 4.0` introduced the `@RestController` annotation in order to simplify the creation of RESTful web services. It’s a convenient annotation that combines `@Controller` and `@ResponseBody`, which eliminates the need to annotate every request handling method of the controller class with the `@ResponseBody` annotation. It simplifies the controller implementation. The controller is annotated with the @RestController annotation; therefore, the @ResponseBody isn’t required. Every request handling method of the controller class automatically serializes return objects into HttpResponse.

- **Content**:

```java
private static final Logger LOG = LoggerFactory.getLogger(CompositeController.class);
private WebClient webClient;
```

A Logger is declared to log debug information, and an instance of `WebClient` is instantiated to make HTTP requests.

```java
@Value("${SERVICE_DATE_SERVICE_URL}")
String urlDate;

@Value("${SERVICE_TIME_SERVICE_URL}")
String urlTime;
```

These lines use the `@Value` annotation to inject values from the application's configuration properties. Specifically, `${SERVICE_DATE_SERVICE_URL}` and `${SERVICE_TIME_SERVICE_URL}` are placeholders for the URLs of the date and time services, respectively. By using `@Value`, the actual values of these URLs are retrieved from the application's configuration files at runtime and assigned to the `urlDate` and `urlTime` variables.

```java
public CompositeController(WebClient.Builder builder) {
    webClient = builder.build();
}
```

We see that the **constructor** accepts a `WebClient` builder as a parameter and constructs a `WebClient` instance. WebClient is a component of Spring WebFlux used for making asynchronous HTTP calls. It allows for building HTTP requests fluently and handling responses reactively. `WebClient.Builder` is a constructor that provides a convenient way to configure a `WebClient` instance. The `builder.build()` method creates the instance that can be used for making HTTP requests.

```java
@GetMapping(value = "/datetime")
public Mono<LocalDateTimeWithTimestamp> dateTime() throws InterruptedException {
  ...
}
```

This method is mapped to the `/datetime` endpoint and returns a `Mono` of `LocalDateTimeWithTimestamp`. `Mono` is a part of the `Reactor` framework used for reactive programming. A `Mono` represents a single asynchronous component that can be empty or represent a single value or error.

```java
LOG.info("Calling time API on URL: {}", urlTime);
Mono<LocalTimeWithTimestamp> localTimeMono = webClient.get().uri(urlTime).retrieve()
        .bodyToMono(LocalTime.class)
        .map(time -> new LocalTimeWithTimestamp(time, LocalTime.now()));

LOG.info("Calling time API on URL: {}", urlDate);
Mono<LocalDateWithTimestamp> localDateMono = webClient.get().uri(urlDate).retrieve()
        .bodyToMono(LocalDate.class)
        .map(date -> new LocalDateWithTimestamp(date, LocalTime.now()));
```

Here, calls to the date and time services are made. For each, the URL is first constructed, a log message is recorded, and then an **HTTP GET** request is made to the specified URL. The responses are then mapped to the respective objects of type `LocalTimeWithTimestamp` and `LocalDateWithTimestamp`, which respectively contain the time and date received from the corresponding services.

More precisely, our code sends an `HTTP GET` to `urlTime` and maps the JSON response to a `LocalTime` object. Subsequently, the result is mapped to an object of type `LocalTimeWithTimestamp`.

```java
return Mono.zip(localDateMono, localTimeMono,
        (localDate, localTime) -> new LocalDateTimeWithTimestamp(localDate, localTime));
```

Once the **HTTP GET** requests are made, the two `Mono` objects are combined using `Mono.zip`, creating a new `LocalDateTimeWithTimestamp` object. In our example, the `dateTime` method returns a `Mono<LocalDateTimeWithTimestamp>`, which means the response will be a single `LocalDateTimeWithTimestamp` object emitted asynchronously.

In summary, `Mono` in our example allows us to react to the results of the HTTP calls once they are available and combine these results smoothly.

#### CompositeApplication

```java
package com.baeldung.composite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class CompositeApplication {

	@Bean
	public WebClient.Builder loadBalancedWebClientBuilder() {
		return WebClient.builder();
	}

	public static void main(String[] args) {
		SpringApplication.run(CompositeApplication.class, args);
	}

}
```

We observe that the `@LoadBalanced` annotation has been removed compared to the original. This annotation is used in a Spring application to enable load balancing between service instances. However, when using Kubernetes, load balancing is typically managed at the infrastructure level rather than at the application level.

Instead of using `@LoadBalanced`, it is more common in a Spring application deployed on Kubernetes to configure load balancing using Kubernetes services. Kubernetes provides the ability to define services of type `ClusterIP`, `NodePort`, or `LoadBalancer`, which handle load balancing for the pods belonging to the service.

The `@SpringBootApplication` annotation indicates that it is the main class of a Spring Boot application.

The `loadBalancedWebClientBuilder()` method is annotated with `@Bean`, indicating that it produces a bean of type `WebClient.Builder`. This method provides a `WebClient.Builder` instance that can be used to create a `WebClient` object for making HTTP requests.

#### application.yaml

```yaml
server:
  port: 8080
```

It set the server port to `8080`.

```yaml
spring:
  application:
    name: composite-service
  cloud:
    kubernetes:
      discovery:
        enabled: true
        all-namespaces: true # set to true if services are in different namespaces
```

It sets the application name to `composite-service` and than configures Spring Cloud Kubernetes integration. It enables **service discovery** within Kubernetes by setting `discovery.enabled` to `true`. Additionally, `all-namespaces` is set to `true` to enable discovery across all namespaces if services are located in different namespaces. 

```yaml
logging:
  level:
    org.springframework: INFO
    com.example: DEBUG
```

Sets the logging level for different packages. It configures the logging level for `org.springframework` to `INFO` and for `com.example` to `DEBUG`, allowing for more detailed logging for classes in the `com.example` package.

```yaml
service:
  date-service:
    url: ${SERVICE_DATE_SERVICE_URL}
  time-service:
    url: ${SERVICE_TIME_SERVICE_URL}
```

Defines properties for the URLs of the date and time services (`service.date-service.url` and `service.time-service.url`).

The `${SERVICE_DATE_SERVICE_URL}` and `${SERVICE_TIME_SERVICE_URL}` placeholders indicate that the actual URLs are provided externally, such as through environment variables, and are injected into the application during runtime.


### date-service-end

#### DateController

- **import**:

```java
package com.example.date;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.random.RandomGenerator;
```

Compared to other cases, we notice the presence of **imports** related to the `Java Time API` for handling date and time, and `random.RandomGenerator` that provides methods to generate random numbers.

- **Declaration**:

```java
@RestController
public class DateController {
  ...
}
```

We can see that this class defines a **REST controller**, making it capable of handling **HTTP request** and returning **JSON responses**. 

- **Content**:

```java
private static final RandomGenerator RND = RandomGenerator.getDefault();
```

This field is a static instance of `RandomGenerator`, used to generate **random numbers**. It uses the default random generator.

```java
@GetMapping(value = "/date")
public Mono<LocalDate> time() throws InterruptedException {
    return Mono.just(LocalDateTime.now().toLocalDate())
            .delayElement(Duration.ofMillis(RND.nextInt(1000)));
}
```

This annotation maps **HTTP GET request** to the `/date` endpoint to this method.

This method returns a `Mono<LocalDate>`, representing a single asynchronous value that will eventually contain the current date.

In particular, `Mono.just` creates a `Mono` that **emits the current local date** derived from the current date-time (`LocalDateTime.now()`). Then, a **delayElement** is added that introduces a delay to the `Mono`, where the duration of the delay is a **random number of milliseconds** up to 1000 (one second). This simulates a variable response time.

Essentially, the `DateController` class defines a RESTful web service that provides the current local date. The service:

- Handles HTTP GET request to the `/date` endpoint.

- Uses reactive programming to return the date asynchronously.

- Introduces a random delay of up to one second before returning the date, simulating a delay that might occur in real-world scenarios.

#### DateServiceApplication

```java
package com.example.date;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DateServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DateServiceApplication.class, args);
    }
}
```

This is the entry point of the microservice; indeed, by using the `@SpringCloudApplication` annotation, Spring Boot's auto-configuration is enabled, and a Spring context is started.


### time-service-end

Regarding the service referencing the `/time` URL, its functionality and structure are essentially identical to what was previously seen with the `/date` URL.

The only difference lies in the fact that the **date-service-end** service creates a `Mono` containing the current date with a delay ranging from ${0ms}$ to ${1000ms}$, whereas the **time-service-end** service creates a `Mono` containing the current time with a delay ranging from ${0ms}$ to ${2000ms}$.


### pom.xml

The file is a Maven pom.xml for a multi-module project.

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
```

It explicitly specifies that the file is a Maven `pom.xml` with the correct namespace for `Maven 4.0.0`, detailing the version of the POM model used.

```xml
<name>spring-cloud-discovery-end</name>
<description>test kubernetes for spring-cloud-discovery-end </description>
```

There is a brief description and the name of the project.

```xml
<modules>
    <module>time-service-end</module>
    <module>date-service-end</module>
    <module>composite-service-end</module>
</modules>
```

`<modules>` contains a list of Maven **modules** that make up the multi-module project. Each module represents a sub-project or module of the main project.

When running a Maven command in the directory containing this `pom`, Maven **executes the command on all modules listed** in <modules>. This facilitates the management of complex projects with multiple components.

With this `pom`, a Maven multi-module project is defined to manage a set of Spring services (`time-service-end`, `date-service-end`, `composite-service-end`). Each module is a Maven sub-project with its own `pom`, dependencies, and configuration, but all are centrally managed through this aggregate `pom`.

### ./kube

To run our project on Kubernetes, we need to create **Kubernetes resources**.

In other words, you describe how you want the deployment of your application to look like, and Kubernetes figures out the necessary steps to reach this state.

Kubernetes resources are defined using **YAML file** and are submitted to the cluster through interaction with `kubectl`.

The resources we need are `Deployment` and `Service`.

As a first step, create a directory named `kube` with the purpose of containing all the **YAML files** we will create. Grouping all resource definitions within a single folder is a **best practice** that allows us to submit all configurations to the cluster with a single command.

#### Deployment

The first Kubernetes resource we need is a **Deployment**, which is used to create and manage running instances (pods) of containers.

A Kubernetes Deployment is a resource that helps manage and scale multiple identical pods in a cluster. It automates the creation, management, and scaling of pod replicas, ensuring that the **desired state** of the application is maintained. This simplifies the process of deploying, updating, and scaling applications without needing to handle infrastructure manually.

For example, instead of manually managing replicas of a web application, you define the desired state (like the number of replicas or container image version) in the Deployment. Kubernetes then ensures the application runs as specified, automatically replacing any failed replicas.

Deployments also support features like rolling updates, rollbacks, and scaling, which help manage application updates with minimal downtime and allow for easy adjustment of resources based on demand.

- Example (`date-service-deployment.yaml`):

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: date-service
spec:
  replicas: 1
  selector:
    matchLabels:
      app: date-service
  template:
    metadata:
      labels:
        app: date-service
    spec:
      containers:
        - name: data-service
          image: aguz99aa/date-service
          ports:
            - containerPort: 8080
          env:
            - name: TZ
              value: "Europe/Rome"  # set the desired timezone
```

- **apiVersion:** Specifies the Kubernetes API version (`apps/v1`).  
- **kind:** Defines the type of Kubernetes resource (`Deployment`).  
- **metadata:** Contains metadata like the `name`.  
- **spec:** Specifies the desired state for the Deployment.  
  - **replicas:** Specifies the number of instances to run (1 replica in this case).  
  - **selector:** Specifies how the Deployment selects which Pods to manage.  
    - **matchLabels:** Matches Pods with labels (`app: date-service`).  
  - **template:** Specifies the Pod template used to create new Pods.  
    - **metadata:** Labels Pods with `app: date-service`.  
    - **spec:** Defines the specification of the Pod.  
      - **containers:** Defines the containers within the Pod.  
        - **name:** Specifies the name of the container (`data-service`).  
        - **image:** Specifies the Docker image (`aguz99aa/date-service`) to use.  
        - **ports:** Specifies ports to expose within the container (`8080`).  
        - **env:** Specifies environment variables for the container (`TZ` set to `"Europe/Rome"`). **In tests done without specifying the timezone the time was often not the correct one, even though those of kubectl and minikube were set correctly**.

A Deployment runs an app within the cluster, but it doesn't make it available to other apps. To expose an application to others, we need a **Service**.

#### Services

A **Service** makes a Pod accessible to other Pods and to users outside the cluster. Without it, a Pod is not accessible from any other source. Kubernetes assigns Pods private IP addresses as soon as they are created in the cluster. These IP addresses are **not permanent**. If Pods are deleted or recreated, they get a new IP address, different from the one they had before. This is problematic for a client that needs to connect to a Pod.

This is where **Kubernetes Services** come in. The Service can be reached at the same place, at any point in time. So it serves as a **stable destination** that the client can use to get access to what it needs. The client doesn’t have to worry about the Pods’ dynamic IP addresses anymore. 

- Example (`date-service-service.yaml`):

```yaml
apiVersion: v1
kind: Service
metadata:
  name: date-service
spec:
  selector:
    app: date-service
  ports:
    - port: 8080
      targetPort: 8080
  type: LoadBalancer
```

- **apiVersion:** Specifies the Kubernetes API version (`v1` for Service).
- **kind:** Defines the type of Kubernetes resource (Service).
- **metadata:** Contains metadata like the name of the Service (`date-service`).
- **spec:** Specifies the desired state for the Service.
  - **selector:** Specifies which Pods will be targeted by this Service.
    - **app: date-service:** Selects Pods with the label `app: date-service`.
  - **ports:** Specifies the ports that the Service will expose.
    - **port:** Specifies the port on the Service (`8080`).
    - **targetPort:** Specifies the port on the Pod that the Service will forward traffic to (`8080` in this case).
  - **type:** Specifies the type of Service (`LoadBalancer`).
    - **LoadBalancer:** Exposes the Service externally using a cloud provider's load balancer. This makes the Pod accessible from outside the cluster. The default type is `ClusterIP`, which makes the Pod accessible only from within the same cluster.

#### Service types

In Kubernetes, there are three commonly used Service types: **ClusterIP**, **NodePort**, and **LoadBalancer**. These Services provide different ways to make Pods accessible.

1. **ClusterIP**: In Kubernetes, the ClusterIP Service is used for **Pod-to-Pod communication** within the same cluster. This means that a client running outside of the cluster cannot directly access a ClusterIP Service. When a ClusterIP Service is created, it is assigned a **static IP address**. This address remains the same for the lifetime of the Service. When a client sends a request to the IP address, the request is automatically routed to one of the Pods behind the Service. If multiple Pods are associated, the ClusterIP Service uses load balancing to distribute traffic equally among them.

2. **NodePort**: The NodePort Service is a way to expose your application to external clients. An external client is anyone who is trying to access your application from outside of the Kubernetes cluster. The NodePort Service does this by opening the port you choose (**from 30000 to 32767**) on all worker nodes in the cluster. This port is what external clients will use to connect to your app.

3. **LoadBalancer**: A LoadBalancer Service is another way you can expose your applications to external clients. However, it only works if you're using Kubernetes on a cloud platform that supports this Service type. When a LoadBalancer Service is, Kubernetes detects which cloud computing platform your cluster is running on and creates a load balancer in the infrastructure of the cloud provider. The load balancer will have its own unique, publicly accessible IP address that clients can use to connect to your application. LoadBalancer Service builds on top of the NodePort Service, with an added benefit. In fact it adds load balancing functionality to distribute traffic between nodes. This **reduces** the negative effects of any one node **failing** or becoming **overloaded** with requests.

#### Best Practice

It is a **best practice** to store resources related to the same application within the **same YAML file**. What we will do is create a single YAML file and inside it, separate the **Deployment and Service sections** as follows:

- Example (`date-service.yaml`):

```YAML
# Deployment YAML definition
apiVersion: apps/v1
kind: Deployment
metadata:
  name: date-service
spec:
  replicas: 1
  selector:
    matchLabels:
      app: date-service
  template:
    metadata:
      labels:
        app: date-service
    spec:
      containers:
        - name: data-service
          image: aguz99aa/date-service
          ports:
            - containerPort: 8080
          env:
            - name: TZ
              value: "Europe/Rome"  # set the desired timezone
---
# Service YAML definition
apiVersion: v1
kind: Service
metadata:
  name: date-service
  #namespace: myapp-namespace
spec:
  selector:
    app: date-service
  ports:
    - port: 8080
      targetPort: 8080
  type: LoadBalancer
```

## Minikube

In this example, we will use **Kubernetes** through **Minikube**. We will have Minikube utilize **Docker** to create and manage the container on which Kubernetes will run. The smartest way to operate is to open four terminals: one to interact with Minikube and Kubectl, and one for each service.

1. First, **start the cluster** in Terminal-1:

```sh
minikube start
```

2. Once the cluster is running, you can use `kubectl` to interact with it. For example:

```sh
kubectl cluster-info
```

3. Now we have to clean up any previous build artifacts and then rebuild the project.

```sh
mvn clean package
```

4. In this example, we used DockerHub as the container registry. The first step is to create your DockerID, which essentially represents a username. Once you have your Docker ID, you have to authorise Docker to connect to the DockerHub account:

```sh
docker login
```

5. Now we will operate on the other terminals. Images uploaded to DockerHub must have a name like `username/image:tag`, where `username` is your DockerID, `image` is the name of the image and `tag` is an attribute that is used to indicate the version of the image:

```sh
docker build -t <username>/<name>:<tag> .
```

  - Now we can upload our image to Docker Hub:

```sh
docker push <username>/<name>:<tag>
```

  - Terminal-2:

```sh
docker build -t <username>/composite-service:latest .
docker push <username>/composite-service:latest
```

  - Terminal-3:

```sh
docker build -t <username>/date-service:latest .
docker push <username>/date-service:latest
```

  - Terminal-4:

```sh
docker build -t <username>/time-service:latest .
docker push <username>/time-service:latest
```

6. Now we need to run the `kubectl apply` command, which is used to apply a configuration to a resource by file name or `stdin`. In our case, we have all the configurations in our `kube` directory, so we can apply them all with a single command in Terminal-1: 

```sh
kubectl apply -f kube
```

7. In `Terminal-1`, we  could run the following command, which continuously watches for changes in the status of the pods. In our case it is useful for checking when all the pods reach the `Running` state:

```sh
kubectl get pods --watch
```

  - Example:

```sh
NAME                             READY   STATUS    RESTARTS   AGE
CompositeService                 1/1     Running   0          2m
TimeService                      1/1     Running   0          1m
DateService                      1/1     Running   0          1m
```

8. Now, if all the Pods are correctly in the `Running` state, we have everything ready and working. What we need to do is run the following command, which is used to get the URL of a service in the `Running` state within a Minikube cluster:

  - Terminal-2:

```sh
minikube service composite-service --url
```

  - Terminal-3:
  
```sh
minikube service date-service --url
```

  - Terminal-4:
  
```sh
minikube service time-service --url
```

  - Minikube will return URLs that can be used to access services from our local machine. This is useful for testing and development purposes. To access the services, we need to visit the provided URL and append `/datetime`, `/date`, or `/time` to the end of the URLs.

  It is necessary to repeat the command for each service we want to access.





