# Kubernetes

## 1. Introduction

### 1.1. What is Kubernetes?

Kubernetes is an **open-source** platform designed to automate the deployment, scaling, and management of containerized applications. It has become the **de facto standard** for container orchestration due to its ability to manage containers at scale, allowing organizations to efficiently manage and scale distributed applications.

Kubernetes was developed by Google and became open-source in 2014. Based on Google’s experience with container orchestration, it was donated to the Cloud Native Computing Foundation (CNCF) in 2015, which has since been supporting its development and promotion as the standard for container management.

### 1.2. Benefits and Motivations

Kubernetes is primarily a **container orchestrator**, meaning it can scale applications on a large scale efficiently and reliably. We can imagine a container orchestrator as a **highly-skilled Tetris player**, where containers are the blocks, and servers are the boards.

One of the key benefits of Kubernetes is its ability to automate many operations, which improves efficiency and reduces manual workload. It simplifies application scaling, both horizontally (adding more instances) and vertically (increasing resources for existing instances). Moreover, Kubernetes maintains the desired state of applications, ensuring that the number of replicas and other configurations remain consistent even in case of failures or changes.

In addition, Kubernetes offers great portability, allowing containerized applications to run on any infrastructure, whether cloud-based or on-premises. It also streamlines the process of updating applications through mechanisms like rolling updates and rollbacks, ensuring that applications can be updated without significant downtime, and issues can be resolved quickly.

## 2. Kubernetes Architecture

A running Kubernetes deployment is called a **Cluster**, which is a group of hosts running Linux containers managed by Kubernetes. The architecture of a Kubernetes cluster is based on a **master-worker** model, where there is a master, known as the **Control Plane Node**, and one or more **worker nodes**. These worker nodes host **pods** that run user workloads, while the Control Plane manages the worker nodes and coordinates cluster-wide operations.

### 2.1. Control Plane

The **Control Plane** in Kubernetes is responsible for making global decisions about the cluster, such as scheduling and responding to events. Control Plane components can run on any cluster node but are often deployed on a separate machine from the user workloads. That are also **multi-master** configuration to ensure high availability and failover. The Control Plane has several components, the main ones are:

- **Kube-APIserver** ${\rightarrow}$ This component exposes the Kubernetes API. It serves as the frontend of the Control Plane and is designed to scale horizontally, so multiple instances of `Kube-APIserver` can be deployed. It's also possible to balance traffic across them.

- **Etcd** ${\rightarrow}$ It is a redundant **key-value** database used to store all the information of the cluster. It's crucial to have a backup strategy for **etcd** to protect the cluster's state.

- **Kube-Scheduler** ${\rightarrow}$ Responsible for assigning pods to available nodes. It considers factors such as pod resource requirements, hardware and software constraints, and preferences to determine the best allocation.

- **Kube-Controller-Manager** ${\rightarrow}$ Manages controllers that are responsible for maintaining the desired state of the cluster. For example, the *Node Controller* monitors nodes, the *Replication Controller* ensures the correct number of pod replicas, and the *Service Account Controller* manages service accounts and access tokens.

- **Cloud-Controller-Manager** ${\rightarrow}$ Handles interactions with the cloud provider if the cluster is running on a cloud infrastructure. If the cluster is on-premises, this component is unnecessary. The *Cloud Controller Manager* manages cloud-specific resources like nodes and load balancers.

### 2.2. Worker Nodes

The worker nodes in Kubernetes are where application containers run. Each worker node hosts pods, which contain one or more containers and provide the necessary runtime environment. The worker nodes communicate with the Control Plane, which assigns them pods to run and manages their configurations. The main components run on each node and are responsible for executing pods and managing networking. The main ones are:

- **Kubelet** ${\rightarrow}$ An agent on each cluster node that ensures containers are running in a pod and verifies that pods are running correctly. *Kubelet* does not manage containers that are not created by Kubernetes.

- **Kube-proxy** ${\rightarrow}$ A network proxy running on each node that handles **networking rules** for Kubernetes Services. These rules enable communication between nodes and with external systems.

- **Container Runtime** ${\rightarrow}$ Software responsible for running containers. Kubernetes supports multiple container runtimes, such as *Docker* and *containerd*.

More details about Kubernetes components can be found at: `https://kubernetes.io/docs/concepts/overview/components/`

### 2.3. Addons

In Kubernetes, **addons** are additional components that extend the cluster's functionality. These components are often essential for cluster operations and management. Some core addons include *DNS* for internal name resolution, the *Kubernetes Dashboard* for web-based management, and *monitoring* and *logging* tools for centralized container metrics and logs. Lots of addons can be included. The most popular ones include:

- **Networking** ${\rightarrow}$ *Calico*, *Weave Net*.

- **Monitoring and Logging** ${\rightarrow}$ *Prometheus*, *Grafana*, *ELK Stack* (Elasticsearch, Logstash, Kibana).

- **Kubernetes Dashboard** ${\rightarrow}$ A web-based UI for managing the cluster.

Most *addons* can be installed using **YAML** configuration files that describe the necessary Kubernetes resources (pods, services, deployments). The files can be applied using `kubectl apply -f <file>.yaml`, although it is a **best practice** to place them all within the same `kube` directory and apply the entire folder with `kubectl apply -f kube`.

If you are using **Minikube**, it offers a simple way to enable and manage addons through the `minikube addons` command with its options:

- To list available addons:
```sh
minikube addons list
```

- To enable an addon:
```sh
minikube addons enable <addon-name>
```

- To disable an addon:
```sh
minikube addons disable <addon-name>
```

For example, to enable the **Kubernetes dashboard** in Minikube, you can use:
```sh
minikube addons enable dashboard
```

Add-ons are a powerful way to extend and customize a Kubernetes cluster, providing additional features that assist in the management, monitoring, and operation of the cluster.

For more details, visit:  
- `https://kubernetes.io/docs/concepts/cluster-administration/addons/`  
- `https://github.com/kubernetes`  

### 2.4. Pods

In Kubernetes, we don't talk about containers, but rather **Pods**. **Pods** are **the smallest processing units** that you can create and manage in Kubernetes. A Pod is essentially a **container that encapsulates one or more containers**, with *shared storage and network resources*, and information that define how to run the containers. The contents of a Pod are always co-located and co-scheduled, and executed in a shared context. A Pod represents a specific "logical host" for an application: it contains one or more tightly coupled containers. In non-cloud contexts, applications running on the same physical or virtual machine are analogous to cloud applications running on the same logical host.

In addition to application containers, a Pod can include *init containers*, which are executed during the Pod's startup, and you can also inject *ephemeral containers* for debugging a running Pod.

The shared context of a Pod is a set of *Linux namespaces*, *cgroups*, and potentially other aspects of isolation, the same ones that isolate a container. Within the context of a Pod, individual applications can have additional layers of isolation. Essentially, a Pod is a set of containers with shared namespaces and file system volumes.

In Kubernetes, we never manage containers directly, but rather the Pods that contain them. From a technical perspective, `Pods` are resources like `Deployments` and `Services`.

Often a Pod contains a single container. In this case, we refer to the **"one-container-per-pod"** model, which is the most common usage in Kubernetes. In this scenario, we can think of the Pod as a wrapper that envelops a single container. Kubernetes manages the Pod instead of managing the containers directly. In more complex cases, a Pod may contain more than one container; this is referred to as **"co-located containers,"** but these containers must work together closely and share resources. In such cases, Kubernetes treats them as a single unit: they are started and stopped together and must run on the same node.

Typically, it is not necessary to create Pods directly, since they are designed as **disposable entities**. When a Pod is created, it is scheduled on a node in the cluster. The Pod remains on that node until its execution completes, the Pod object is deleted, the Pod is evicted due to lack of resources, or the node fails.

It is better to create Pods using workload resources like **Deployments**. Each Pod is designed to run a single instance of an application. If you want to scale the application horizontally, you need to create multiple Pods, each with its own instance of the application. In Kubernetes, this is called **replication**. Replicated Pods are usually created and managed as a group by a workload resource and its controller.

Regarding **storage**, a Pod can specify a set of shared volumes. All containers within the Pod can access the shared volumes, allowing data sharing. Volumes also allow data to be persistently stored in case one of the containers within the Pod restarts.

Regarding **networking**, each Pod has a unique IP address. All containers within a Pod share the same network namespace, including the IP address and network ports. Containers within the same Pod can communicate with each other using `localhost`. When the containers of a Pod communicate with external entities, they must coordinate the use of shared network resources (like ports). Containers that need to communicate with others in different Pods can do so via the IP network.

To set **security constraints** on Pods and containers, the `securityContext` field is used in the Pod specification. This field provides you with granular control over what a Pod or individual containers can do.

A Pod can be in five possible states:
1. **Pending** ${\rightarrow}$ The Pod has been created but is not yet running.  
2. **Running** ${\rightarrow}$ The Pod and its containers are running without issues.  
3. **Succeeded** ${\rightarrow}$ The Pod has completed the lifecycle of its containers and has terminated correctly after the expected operations are finished.  
4. **Failed** ${\rightarrow}$ At least one container within the Pod has terminated abnormally due to an error, consequently the Pod has been terminated.  
5. **Unknown** ${\rightarrow}$ The state of the Pod cannot be determined.

The `kubectl` commands allow you to obtain information about Pods and their status for a specific application.

Further information can be found on the official Kubernetes website: `https://kubernetes.io/docs/concepts/workloads/pods/`

### 2.5. Services

In Kubernetes, a **Service** is a method for exposing a network application that runs as one or more Pods within the cluster.

One of the key **goals of services** in Kubernetes is that you don’t need to modify your existing application to use an unknown service discovery mechanism. You run your code in Pods (whether it’s cloud-native code or an older application that you’ve containerized) and you use a *service* to make that group of Pods accessible on the network so that clients can interact with it.

If you use a **Deployment** to run your application, that deployment can create and destroy Pods dynamically. As a result, you don’t always know how many of those Pods are active and running. Kubernetes creates and destroys Pods to align with the desired state of the cluster; in fact, Pods are ephemeral resources. This creates a challenge:*If some Pods (backends) provide functionality to other Pods (frontends) within the cluster, how do the frontends discover and keep track of the IP addresses they need to connect to the backend?*

Kubernetes' **Service API** is an abstraction that helps you expose groups of Pods over a network. Each *Service* object defines a logical set of endpoints (typically these endpoints are Pods) along with a policy on how to make those Pods accessible.

Essentially, when there are multiple replicas of a *backend* functionality, the *frontend* doesn’t need to worry about which specific Pod or replica it is connecting to. If the Pods or *backend* replicas change, this process should be transparent to the *frontend*. This is made possible by the abstraction provided by Kubernetes’ *Service API*, which enables this decoupling.

The set of Pods targeted by a *Service* is typically identified through a **selector** defined by the user. However, it is also possible to configure *Services* without a selector.

If the application uses HTTP, you can use **Ingress** to control web traffic to the workload. *Ingress* is not a type of Service; it is a resource that manages external access to internal services within a cluster. *Ingress* acts as an entry point for the cluster, allowing you to consolidate routing rules and expose multiple workload components behind a single listener. In short, it enables you to manage and configure *HTTP/HTTPS* access to various Services, centralizing and simplifying control over incoming traffic.

In conclusion, if an application can use Kubernetes APIs for **service discovery**, you can query the *API server* to obtain the corresponding **EndpointSlices** to reach the desired service. Kubernetes updates a Service's *EndpointSlices* whenever the set of Pods in the service changes.

More information on Services can be found at: `https://kubernetes.io/docs/concepts/services-networking/service/`

### 2.6. ConfigMaps & Secrets

A **ConfigMap** is an API object used to store non-confidential data in *key-value pairs*. A *ConfigMap* is a resource used to manage external configurations for container images. In other words, instead of embedding specific configurations (such as environment variables, configuration files, or parameters) directly into the code or the container image, these are externalized into a *ConfigMap*.

Different configurations, such as production or development environment settings, can be managed separately without modifying the container image. This allows you to reuse the same container image across different environments by simply changing the configuration through the *ConfigMap*.

For example, imagine developing an application that you can run both on your local machine (for development) and in the cloud (to handle real traffic). You write the code to look for an environment variable named `DATABASE_HOST`. Locally, you set that variable to `localhost`. In the cloud, you configure it to point to a *Service* that exposes the database component to the cluster. This allows you to debug the same code locally while using the same container image that runs in the cloud.

**Note**: *ConfigMap does not provide secrecy or encryption. If the data you need to store is sensitive, use a Secret instead of a ConfigMap, or additional tools to keep your data private.*

A **Secret** is an object that contains a small amount of sensitive data such as a password, token, or key. These details might otherwise be included in a Pod specification or within a container image. Using a *Secret* ensures that you don’t need to embed sensitive information in your application code.

Since *Secrets* can be created independently of the Pods that use them, there is less risk of the Secret (and its data) being exposed during the process of creating, viewing, and modifying Pods. Kubernetes and applications running in the cluster can take additional precautions with *Secrets*, such as avoiding writing sensitive data to non-volatile storage.

*Secrets* are similar to *ConfigMaps*, but they are specifically designed to hold confidential data. For example, they can be used to set environment variables for a container, provide credentials like SSH keys or passwords to Pods, or allow the `kubelet` to pull container images from private registries.

**Warning**: *By default, Kubernetes Secrets are stored unencrypted in the underlying data store of the API server (etcd). Anyone with access to the API can retrieve or modify a Secret, as can anyone with access to etcd. Additionally, anyone authorized to create a Pod in a namespace can use that access to read any Secret in that namespace.*

More information about *Secrets* and *ConfigMaps* is available on the official Kubernetes site:  
- `https://kubernetes.io/docs/concepts/configuration/configmap/`  
- `https://kubernetes.io/docs/concepts/configuration/secret/`

---

### 2.7. Namespaces

In Kubernetes, **namespaces** provide a way to organize and isolate groups of resources within a single cluster. Resources in a namespace must have **unique names**, but the **same name** can be used across **different namespaces**. Namespaces affect the resources that belong to them, such as *Deployments* and *Services*, but not cluster-wide resources like *StorageClasses*, *Nodes*, or *PersistentVolumes*.

Namespaces are particularly useful in environments with many users, teams, or projects. If your goal is to manage a cluster with only a few users, creating or managing namespaces may not be necessary. However, as complexity increases, namespaces can provide important functionality. It’s important to note that namespaces cannot be nested, and each Kubernetes resource belongs to only one namespace. Namespaces can also divide cluster resources among multiple users using *resource quotas*.

To distinguish **slightly different resources**, such as **different versions** of the same software, it is preferable to use **labels** within the **same namespace** rather than creating separate namespaces.

**Warning**: *For production clusters, avoid using the default namespace. Instead, create dedicated namespaces for better organization.*

Kubernetes includes **four default namespaces**:  
1. **Default** ${\rightarrow}$ Used by default for any new cluster until a new one is created.  
2. **Kube-node-lease** ${\rightarrow}$ Contains objects (called *Leases*) related to nodes. These allow the *Kubelet* to send *heartbeats* so the *Control Plane* can detect node failures.  
3. **Kube-public** ${\rightarrow}$ This namespace is **public**, meaning it’s accessible by all users, including unauthenticated users. It’s reserved for resources that need to be publicly visible across the cluster.  
4. **Kube-system** ${\rightarrow}$ Reserved for objects created by the Kubernetes system.  

#### 2.7.1 Using Namespaces

Namespaces isolate resources within the same cluster. Below are some of the key functionalities provided by Kubernetes for managing namespaces:

- To list all the namespaces in the cluster:
```bash
kubectl get namespaces
```

- To create a new namespace:
```bash
kubectl create namespace <namespace-name>
```

- To operate within a specific namespace, you need to change the current context:
```bash
kubectl config set-context --current --namespace=<namespace-name>
```

For more information on **Kubernetes namespaces**, visit: `https://kubernetes.io/docs/concepts/overview/working-with-objects/namespaces/`

## 3. Application Deployment and Management

In *Kubernetes*, the **deployment** of applications is a well-organized process that leverages a set of resources to manage the state, scalability, and reliability of containerized applications.

One of the key features offered by Kubernetes is its ability to automatically manage the application lifecycle, including *initial deployment, updates, scaling, and rollbacks*.

### 3.1. Definition and Implementation of Deployments

To run our project on Kubernetes, we must create **Kubernetes resources**.

In other words, you describe how you want your application deployment to be, and Kubernetes takes care of determining the necessary steps to achieve that state.

Kubernetes resources are defined using `.yaml` files, which are then submitted to the cluster through interactions with `kubectl`.

The resources we need are `Deployment` and `Service`.

#### 3.1.1. Deployment

The first Kubernetes resource we need is a **Deployment**, which is used to create and manage container instances (pods).

A *Kubernetes Deployment* is a resource that helps manage and scale pods and their corresponding replicas within a cluster. It automates the creation, management, and scaling of pod replicas, ensuring that the **desired state** of the application is maintained. This simplifies the process of deploying, updating, and scaling applications without the need for manual infrastructure management.

For example, instead of manually managing the replicas of a web application, you define the **desired state** (such as the number of replicas or the version of the container image) in the Deployment. Kubernetes ensures that the application runs as specified, automatically replacing any replicas that are not functioning correctly.

Deployments also support features like **rollouts**, **rollbacks**, and **scaling**, which help manage application updates with minimal downtime and allow for easy resource scaling based on demand.

A *Deployment* runs an application within the cluster but does not make it available to other applications. To expose an application to others, we need a **Service**.

#### 3.1.2. Services

A **Service** makes a pod accessible to other pods and users outside the cluster. Without it, a pod is not reachable. Kubernetes assigns private IP addresses to pods as soon as they are created in the cluster. These IP addresses are **non-permanent**. If pods are deleted or recreated, they receive new IP addresses different from the ones they had before. This poses a challenge for clients needing to connect to a pod.

This is where **Kubernetes Services** come into play. The *Service* can be easily accessed at any time, acting as a **stable endpoint** that clients can use to access specific functionality. Clients no longer need to worry about the dynamic IP addresses of pods.

#### 3.1.3. Best Practice

It is a **best practice** to store resources related to the same application within the **same** `.yaml` file. What we will do is create a single file and separate the **Deployment and Service sections** within it.

A second **best practice** is to group all `.yaml` files within a single directory called `/kube`. Grouping all resource definitions into a single directory allows us to submit all configurations to the cluster with a single command.

```bash
kubectl apply -f kube
```

### 3.2. Managing State and Updates

#### 3.2.1. Self-healing

**Self-healing** is one of the most powerful features of Kubernetes. It is designed to ensure the availability and resilience of distributed applications. Kubernetes continuously monitors the state of resources and takes automatic corrective actions to maintain the desired state of the cluster. Essentially, the **self-healing** feature involves:
- **Restarting pods and containers** in the event of a **failure**.  
- **Replacing** and **rescheduling pods** in case of failure of one or more nodes.  
- Maintaining the correct **number of replicas**.
- **Terminating containers or pods** that do not pass user-defined health checks and keeping them unavailable until they are ready to serve properly.

A fundamental component that ensures this functionality is the **ReplicaSet**. This is a controller, managed by the **Controller Manager**, responsible for ensuring that a specified number of replicas are always running. If a pod crashes or fails, the *ReplicaSet* detects it and creates a new pod to restore the desired number of replicas. However, the *ReplicaSet* **does not directly manage the internal health of containers**; it monitors only the presence of pods.

Here, **Kubelet** plays a crucial role. This is the agent that runs on each node in the cluster and ensures that the pods running on that node are functioning as expected. Unlike the *ReplicaSet*, it checks the internal health of the pods. Within the specifications of each pod, the following are defined:
- **Liveness Probe** ${\rightarrow}$ used by *Kubelet* to check the health and availability of that pod. If the container fails this check, *Kubelet* terminates it and starts a new one.
- **Readiness Probe** ${\rightarrow}$ used to determine if a container is ready to receive traffic or requests. If it fails this check, *Kubelet* removes the container from the service, preventing it from receiving traffic until it passes the test again.

Another component that supports this functionality is the **Scheduler**, which is responsible for scheduling pods onto available nodes in the cluster. When a node becomes unavailable, the scheduler reschedules the pods that were running on that node onto other available nodes in the cluster.

The collaboration between these components enables *self-healing*, allowing Kubernetes to autonomously restore the desired state of the cluster.

More information can be found on the official Kubernetes site: `https://kubernetes.io/docs/concepts/workloads/controllers/replicaset/#how-a-replicaset-works`

#### 3.2.2. AutoScaling

With Kubernetes, you can automatically scale applications to accommodate variations in resource demand. In Kubernetes, both **horizontal scaling** (increasing or decreasing the number of replicas) and **vertical scaling** (increasing or decreasing the amount of resources allocated to pods) are possible.

Scaling in Kubernetes can be done **manually** or **automatically**.

For **manual scaling**, horizontal scaling can be performed using the `kubectl CLI`, while vertical scaling requires modifying the amounts of resources assigned to the workload. More information on *manual scaling* is available at the following links:
- `https://kubernetes.io/docs/tutorials/kubernetes-basics/scale/scale-intro/`
- `https://kubernetes.io/docs/tasks/configure-pod-container/resize-container-resources/`

Fortunately, Kubernetes also provides **automatic scaling** capabilities.

For horizontal scaling, there is the **HPA** (Horizontal Pod Autoscaler). This is a resource implemented as a Kubernetes API and a controller. It monitors the number of replicas, adding or removing pods based on workload and custom metrics. The *HPA* continuously monitors the defined metrics for the pods and, based on these metrics and other configured thresholds, calculates the appropriate number of replicas. If the current number of replicas differs from the calculated number, *HPA* scales the application by adding or removing pods. Its main task is to ensure that applications can handle the workload while maintaining optimal resource utilization.

For vertical scaling, there is the **VPA** (Vertical Pod Autoscaler). *VPA* automatically adjusts the CPU and memory resources requested by pods. It monitors the resource usage of the pods and suggests changes to the requested resources. *VPA* can be configured to update existing pods or apply changes only to new pods. Unlike *HPA*, it is not included by default in Kubernetes but is a separate project. You can find it at the following link: `https://github.com/kubernetes/autoscaler/tree/9f87b78df0f1d6e142234bb32e8acbd71295585a/vertical-pod-autoscaler`.

To manage workloads that need to be scaled based on cluster size (such as cluster-dns or other system components), you can use the **CPA** (Cluster Proportional Autoscaler). Its goal is to adjust **the number of nodes** (virtual or physical machines) in the cluster to ensure that there are sufficient resources for all pods. *CPA* monitors the cluster to identify if there are unschedulable pods due to insufficient resources. It adds new nodes to the cluster if there are not enough resources to schedule new pods and removes underutilized nodes.

If you prefer to keep the number of nodes constant and scale vertically based on cluster size, you can use the **CPVA** (Cluster Proportional Vertical Autoscaler). This project is currently in beta and can be found on GitHub: `https://github.com/kubernetes-sigs/cluster-proportional-vertical-autoscaler`.

Additional types of scaling and more details can be found at: `https://kubernetes.io/docs/concepts/workloads/autoscaling/`.

#### 3.2.3. Rollout & Rollback

One mechanism provided by Kubernetes that simplifies application deployment is the ability to record *Deployments* and perform **rollback** when necessary. Within the *Deployment* resources, the desired state for the application is defined through information such as the number of replicas and the pod template.

##### 3.2.3.1. Rollout

The term **rollout** refers to the process of making changes to the application. Kubernetes provides a way to execute rollouts in a controlled manner, allowing you to define the `strategy` with which you want to release the update. This influences how changes propagate across all the Pods referencing that *Deployment*.

We can control the *rollout* mechanism of the `Deployment.yaml` resource using the `strategy` field within the `Spec` section, as shown in the following example:

```yaml
$ cat myapp-deployment-recreate.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: myapp-deployment
spec:
  replicas: 3
  strategy:
    type: Recreate
  selector:
    ...
```

There are mainly two *rollout* strategies:

1. **Recreate** ${\rightarrow}$ This strategy recreates all instances at once. This can be problematic for running services that handle live traffic, as recreating all instances simultaneously causes **downtime**.

2. **RollingUpdate** ${\rightarrow}$ This is the default rollout method. It takes a more gradual approach; instead of recreating all Pods at once, it updates a subset of pods, allowing the rest to continue handling traffic. Additionally, by setting the `maxSurge` and `maxUnavailable` parameters, we can further refine the strategy.

```yaml
...
strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 2
      maxUnavailable: 0
...
```

The **rollout** also allows for an **incremental update**. It replaces replicas of the old version with those of the new version, one at a time or in small batches, to ensure continuous operation and avoid periods of application functionality unavailability.

What is usually done to perform a *rollout* is to modify the `Deployment.yaml` file to reference the new version of the application. Then, the changes are applied using the `kubectl apply` command, and the rollout begins. You can also monitor the rollout to ensure it is proceeding as expected with the command:

```sh
kubectl rollout status deployment/my-deployment
```

##### 3.2.3.2. Rollback

When we talk about **rollback**, we refer to the process of restoring your application to a previously stable state. This is particularly useful if a rollout introduces issues, allowing you to quickly revert to a known and functioning version.

To execute a rollback effectively, you first check the **rollout history** to identify the version you wish to revert to. This can be done using the command:

```sh
kubectl rollout history deployment/my-deployment
```

To actually perform the rollback, you use the command `kubectl rollout undo`, which by default reverts to the immediately preceding version, but you can also specify a particular version if needed.

```sh
kubectl rollout undo deployment/my-deployment
```

Since rollback in Kubernetes is a type of rollout, you can monitor it using the same command as before:

```sh
kubectl rollout status deployment/my-deployment
```

**Rollout** and **rollback** allow you to manage application updates with minimal downtime, recover swiftly from issues, and ensure that your applications remain stable and available during changes.

#### 3.2.4. Service Discovery & Load Balancing

Kubernetes provides built-in mechanisms for **service discovery** and **load balancing** among pods.

In Kubernetes, **Service Discovery** is a fundamental component that ensures microservices can find and interact with each other **without manual configuration**. Kubernetes assigns an IP address to each Pod and a DNS name to the *Services*, enabling traffic to be balanced among them.

##### 3.2.4.1. Service Discovery

You can leverage Kubernetes' *Service Discovery* in two distinct ways:

1. **DNS-Based** ${\rightarrow}$ Kubernetes automatically configures a DNS within the cluster. Each service created in Kubernetes receives a DNS name, allowing Pods to communicate with other services using these names. For instance, consider a service named `my-service` within the namespace `my-ns`. Pods in the `my-ns` namespace should be able to access the service simply using the name `my-service`, while those outside that namespace must specify the fully qualified name `my-service.my-ns`.

2. **Environment Variables** ${\rightarrow}$ When a Pod starts, Kubernetes injects certain environment variables via `kubelet`, containing information about the available services. This method is less flexible than DNS and is typically used in simpler or legacy cases. For example, for the service `my-service`, the variables `MY_SERVICE_SERVICE_HOST` and `MY_SERVICE_SERVICE_PORT` are added, where the service name is converted to uppercase, and hyphens are replaced with underscores.

Essentially, the Kubernetes resources underpinning *service discovery* are the *Services*. To facilitate communication between various Pods, each service must have a corresponding `Service.yaml` file, as shown below:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: my-service
spec:
  selector:
    app: my-app
  ports:
    - protocol: TCP
      port: 80
      targetPort: 8080
```

##### 3.2.4.2. Load Balancing

Regarding **Load Balancing**, in Kubernetes, this refers to the distribution of traffic among the various Pods in a cluster, ensuring uniform resource utilization and enhancing availability and reliability.

There are several types of *load balancing* available in Kubernetes:

1. **Internal Load Balancing** ${\rightarrow}$ When a `ClusterIP` type *Service* (default) is created, an internal *load balancer* is defined. Traffic sent to the cluster's IP address is distributed among all the **healthy** Pods that match the service's selector.

2. **External Load Balancing**:
  
   2.1. To expose services to external clients and implement load balancing features, Kubernetes provides the `LoadBalancer` service type, which interacts with the cloud provider to create an external load balancer that distributes incoming traffic to the Pods.

   2.2. Another way to expose a *Service* externally is through `NodePort`. This opens a specific port on all nodes, and Kubernetes ensures that traffic directed to that port is evenly distributed among the Pods.

3. **Ingress** ${\rightarrow}$ Provides a way to expose *HTTP* and *HTTPS* services outside the cluster and manage the corresponding traffic. `Ingress` allows for advanced routing rules to direct traffic to different services, functioning as an application-level load balancer.

To discover more about the numerous features of Kubernetes, you are encouraged to visit: `https://kubernetes.io/`

## 4. Kubernetes Configuration and Management

### 4.1. Kubernetes Installation

#### 4.1.1. Minikube

Minikube is a tool that allows you to **create a Kubernetes cluster locally**. It is particularly useful for developers and system administrators who want to test, develop, and experiment with Kubernetes in a local environment before deploying to production. *Minikube* provides a complete and configurable Kubernetes environment.

**Prerequisites**

In addition to some free memory, you’ll need a *Container Manager* like **Docker**, or a virtualization environment like *QEMU*, *Hyper-V*, *KVM*, or *VirtualBox*. This is because Minikube uses one of these to create and manage the virtual machine where Kubernetes runs.

Another required tool is `kubectl`, the command-line utility used to interact with Kubernetes.

**Installation**

To install *Minikube* and *kubectl*, it's recommended to follow the official guides available at the following links:

- `https://minikube.sigs.k8s.io/docs/start/`

- `https://kubernetes.io/docs/tasks/tools/`

**Key Features**

*Minikube* creates a **single-node** Kubernetes cluster, meaning that both the master and worker nodes run on a single virtual machine. This is sufficient for most development and testing use cases. 

Minikube supports a range of optional add-ons that can be enabled to extend the cluster's functionality. These add-ons include *dashboard*, *metrics-server*, *ingress*, and many others.

**Advantages of Minikube**

1. **Local testing environment**: Ideal for development and experimentation without needing to configure a full cluster in the cloud or a production environment.
   
2. **Speed and convenience**: Allows for quick start and stop of the cluster, facilitating the development cycle.

3. **Compatibility**: Supports the same Kubernetes API, ensuring that code developed and tested on Minikube is compatible with production Kubernetes clusters.

**Starting Minikube**

After installation, to start a *Minikube* cluster, run the following command:

```bash
minikube start
```

To verify that the cluster is running and check the status of the nodes, you can use the following commands:

```bash
kubectl cluster-info
```

```bash
kubectl get nodes
```

To delete the *Minikube* cluster and the corresponding virtual machine, use the command:

```bash
minikube delete --all
```

### 4.2. Cluster Management

#### 4.2.1. Node Management

Nodes are the machines that run the pods in the Kubernetes cluster. Here's how to manage them:

- **View nodes:** To get a list of nodes in the cluster:
```bash
kubectl get nodes
```

- **Details of a specific node:** To display detailed information, including its status and available resources on a node, use:
```bash
kubectl describe node <node-name>
```

- **Add or remove nodes:** Minikube is designed for development environments, so node management is less complex than in a production environment. The easiest way to add more nodes is to start *Minikube* with the option to specify the number of nodes:
```bash
minikube start --nodes=2
```

#### 4.2.2. Resource Management

Kubernetes manages several resources within the cluster, such as *pods*, *services*, and *deployments*. Here's how to view these resources:

- **Pods:**
```bash
kubectl get pods
```

- **Services:**
```bash
kubectl get svc
```

- **Deployments:**
```bash
kubectl get deployments
```

- To get detailed information on a specific pod:
```bash
kubectl describe pod <pod-name>
```

- To scale the number of replicas in a deployment:
```bash
kubectl scale deployment <deployment-name> --replicas=<number-of-replicas>
```

- To update the container image in a deployment:
```bash
kubectl set image deployment/<deployment-name> <container-name>=<new-image>
```

#### 4.2.3. Access Control

Access control in Kubernetes is a mechanism to restrict and manage who can do what within the cluster. There are three main components to managing access control in Kubernetes:

1. **Authentication** ${\rightarrow}$ This involves verifying the identity of anyone making a request to the cluster. Kubernetes supports several authentication methods, including *certificates, tokens, or external ID providers*.

2. **Authorization** ${\rightarrow}$ This is the second phase, following authentication. In this phase, Kubernetes checks whether the action the user is trying to perform is allowed for that type of user. Kubernetes supports various authorization models:

   - *Role-Based Access Control (RBAC)*: allows assigning permissions based on roles to users or groups. The key objects in **RBAC** are *Roles* and *ClusterRoles*, which define permissions, and *RoleBindings* and *ClusterRoleBindings*, which associate permissions with users.

   - *Attribute-Based Access Control (ABAC)*: this model is based on rules defined by the cluster administrator.

   - *Webhook Authorization*: delegates authorization decisions to external services.

3. **Admission Control** ${\rightarrow}$ These are plugins that run after the *Authorization* phase and can modify or block requests. They ensure that requests meet certain criteria before being allowed.

The most widely used and recommended authorization model is **RBAC**. There are several reasons why it’s the preferred choice. First and foremost, it offers **ease of management**. It's simpler to configure because it’s based on clear roles and associations, rather than manual attributes. Secondly, it provides excellent **scalability**, as it is very versatile in complex environments with many users due to its well-defined roles. Additionally, *RBAC* is enabled by default, while *ABAC* requires manual configuration and *Webhook Authorization* (WA) requires additional integration as an external service. Below are some useful commands for managing *RBAC*:

- View roles and bindings:
```bash
kubectl get roles -n <namespace>
kubectl get rolebindings -n <namespace>
kubectl get clusterroles
kubectl get clusterrolebindings
```

- Create a *Role*:
```bash
kubectl create role <role-name> --verb=get,list,watch --resource=pods -n <namespace>
```

- Create a *ClusterRole*:
```bash
kubectl create clusterrole <role-name> --verb=get,list,watch --resource=pods
```

- Bind a *Role* to a user or group (*RoleBinding*):
```bash
kubectl create rolebinding <binding-name> --role=<role-name> --user=<user-name> -n <namespace>
```

- Bind a *ClusterRole* to a user or group (*ClusterRoleBinding*):
```bash
kubectl create clusterrolebinding <binding-name> --clusterrole=<role-name> --user=<user-name>
```

- Delete a *Role* or *Binding*:
```bash
kubectl delete role <role-name> -n <namespace>
kubectl delete rolebinding <binding-name> -n <namespace>
kubectl delete clusterrole <role-name>
kubectl delete clusterrolebinding <binding-name>
```

- Examine a user’s permissions:
```bash
kubectl auth can-i <verb> <resource> -n <namespace> --as=<user>
```

You can also create *Roles*, *ClusterRoles*, *RoleBindings*, and *ClusterRoleBindings* using `.yaml` files. These allow for clearer access management configurations:

Example of a `.yaml` file for a *Role*:

```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  name: example-role
  namespace: default
rules:
  - apiGroups: [""]
    resources: ["pods"]
    verbs: ["get", "list", "watch"]
```

Apply the role to the cluster with:
```bash
kubectl apply -f role.yaml
```

Example of a `.yaml` file for a *RoleBinding*:

```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  name: example-rolebinding
  namespace: default
subjects:
  - kind: User
    name: "example-user"
    apiGroup: rbac.authorization.k8s.io
roleRef:
  kind: Role
  name: example-role
  apiGroup: rbac.authorization.k8s.io
```

These files should be organized in a single directory called `/kube` and then applied together using the Kubernetes `apply` command (*Best Practice*).

For more information on *access control*, visit the official site at the following link: 
`https://kubernetes.io/docs/concepts/security/controlling-access/`

## 5. Storage and Persistence

In Kubernetes environments, **storage management** is essential for applications that need to retain data even after pod or node restarts. In a containerized environment, pods can be frequently created and destroyed, so a storage architecture is necessary to ensure data persists beyond the lifecycle of individual pods.

### 5.1. Storage Concepts

In Kubernetes, **storage** is organized around various resources and abstractions. Modern applications often rely on **persistent** data, such as databases or shared file systems, which require storage independent of pod lifecycles. Kubernetes provides several types of storage resources:

- **Volumes** ${\rightarrow}$ On-disk files in a container are ephemeral, which presents some problems when a container crashes or is stopped. Container state is not saved so all of the files that were created or modified during the lifetime of the container are lost. Another problem occurs when multiple containers are running in a Pod and need to share files. The **Kubernetes Volume** abstraction solves both of these problems. A **Volume** is a directory which is accessible to the containers in a pod. A **Volume** is directly associated with the lifecycle of a Pod. Every time the Pod is deleted or moved, the Volume attached to it is removed along with the Pod. Volumes are defined within the Pod specification and are useful for sharing data between containers in a Pod or for maintaining temporary data during the Pod’s lifetime. They are also called **Ephemeral Volumes**. (`https://kubernetes.io/docs/concepts/storage/volumes/`)

- **Persistent Volumes (PV)** ${\rightarrow}$ When a pod ceases to exist, Kubernetes destroys ephemeral volumes; however, Kubernetes does not destroy **Persistent Volumes**. A **PV** is a storage resource that is created and configured within the cluster and has an **independent lifecycle** of any individual Pod that uses the PV. PVs can be provisioned by an administrator and may represent various types of storage, such as local disks, network storage, or cloud storage. (`https://kubernetes.io/docs/concepts/storage/persistent-volumes/`)

- **Persistent Volume Claims (PVC)** ${\rightarrow}$ These are storage requests made by users or applications. When a user creates a *PVC*, they specify the storage characteristics they need, such as capacity and access mode. Kubernetes then searches for a compatible *PV* that meets the PVC’s requirements. If a match is found, the PV is bound to the PVC, making it ready for use by pods.

### 5.2. Storage Lifecycle

The lifecycle of storage resources in Kubernetes can be described as follows:

- **Provisioning** ${\rightarrow}$ This is the stage where **PVs are created**. This can be done **manually** by an administrator who configures the PV based on the application’s needs, or **automatically** through dynamic provisioning, which creates PVs on the fly in response to PVC requests.

- **Binding** ${\rightarrow}$ When a **PVC** is created, Kubernetes runs a binding process to find a PV that meets the PVC’s specifications. If a compatible PV is found, it is bound to the PVC, allowing the pod to access it. If no suitable PV is available, the PVC remains in a *"pending"* state until a valid PV is provided. If no action is taken, this state persists, so a manual PV creation or dynamic provisioning must be enabled.

- **Usage** ${\rightarrow}$ Once a PVC is bound to a PV, pods can use the volume by mounting it into their file system. This enables containers to read and write data persistently, even if the pod is restarted.

- **Reclaiming** ${\rightarrow}$ This stage defines how a PV is handled after its associated PVC is deleted or released, meaning the pod no longer needs access to the data or the claim has been removed. Kubernetes offers three possible reclaim policies:  
  - *Retain*: The PV remains in the cluster but is no longer usable. It requires manual intervention for reuse.  
  - *Recycle*: The PV is wiped clean and made available for reuse by a new PVC.  
  - *Delete*: The PV is removed along with the data it contains.  

### 5.3. Importance of Persistent Storage

Data persistence is crucial for many modern applications. Without proper storage management, data would be lost whenever a pod is restarted or recreated. 

Effectively utilizing Kubernetes' storage resources enables the creation of resilient applications, capable of handling failures and configuration changes without losing critical data. In the context of microservices and container-based architectures, this approach is vital to ensuring service continuity and availability.

## 6. Monitoring and Logging

Monitoring the state of the Kubernetes cluster and the applications running on it is essential for ensuring system stability, performance, and availability. **Monitoring** and **logging** help detect issues, optimize resources, and identify bottlenecks or failures before they become critical.

Various tools can be integrated with Kubernetes to provide comprehensive monitoring and logging capabilities.

### 6.1. Monitoring Tools

The first critical aspect to monitor is the state of the Kubernetes cluster itself: resources like CPU, memory, and disk need constant surveillance to prevent overloads or inefficiencies.

Monitoring tools collect metrics from various cluster components, such as nodes, pods, and containers.

One of the most widely used tools for collecting and visualizing detailed Kubernetes metrics is **Prometheus**. Integrating Prometheus into Kubernetes can be simplified using **Helm**, a package manager that facilitates installation.

#### 6.1.1. Prometheus Integration

Installing **Prometheus** with **Helm** is a simple, standardized way to set up monitoring in Kubernetes. Helm is a Kubernetes package manager that allows for the quick and flexible installation of predefined applications (**charts**).

**1. Install Helm**

If Helm is not already installed, it must be installed before proceeding. Follow the official instructions for your operating system:

`https://helm.sh/docs/intro/install/`

**2. Add the Helm Repository for Prometheus**

Before installing Prometheus, you need to add the official Helm repository for Prometheus:

```bash
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update
```

This command adds the Prometheus **Helm charts** repository and updates the available chart index.

**3. Install Prometheus on the Cluster**

To install Prometheus on your Kubernetes cluster, use the specific Helm chart. If you want to install both **Prometheus** and **Grafana**, you can do it in a single step:

```bash
helm install prometheus prometheus-community/kube-prometheus-stack
```

- `prometheus` is the name of the Prometheus installation.

- `prometheus-community/kube-prometheus-stack` is the Helm chart that includes Prometheus, Grafana, and all required monitoring components.

This command will create all the necessary **Pods**, **Services**, and **ConfigMaps** for running Prometheus and Grafana on the cluster.

**4. Verify the Installation Status**

After installation, you can verify that Prometheus was installed correctly by running the following command to see the created pods:

```bash
kubectl get pods -n default
```

If you specified a different namespace during installation, replace `default` with the name of your namespace.

**5. Access the Prometheus Dashboard**

Once Prometheus is installed, you can access the Prometheus dashboard. Since Prometheus is exposed as an internal service within the cluster, you need to use **port forwarding** to access the dashboard locally:

```bash
kubectl port-forward svc/prometheus-kube-prometheus-prometheus 9090
```

This command allows you to access Prometheus in your browser at:

```
http://localhost:9090
```

**6. Access Grafana**

If you installed Grafana along with Prometheus, you can access Grafana using another port-forwarding command:

```bash
kubectl port-forward svc/prometheus-grafana 3000
```

You can then open Grafana in your browser at:

```
http://localhost:3000
```

The default Grafana credentials are:
- **username**: `admin`
- **password**: `prom-operator`

### 6.2. Logging and Tracing

In addition to monitoring, **logging** is essential to understand the behavior of applications running in the cluster and to trace the root cause of issues. Kubernetes generates vast amounts of logs from containers, pods, and other cluster components. Centralizing and managing these logs is essential for analyzing events and errors.

A typical logging solution for Kubernetes is based on an **ELK Stack** (Elasticsearch, Logstash, Kibana) or **Loki**, a more recent solution.

#### 6.2.1. ELK Stack Integration

The **ELK Stack** (Elasticsearch, Logstash, Kibana) is one of the most popular solutions for centralized log collection and visualization. The ELK Stack enables the aggregation and analysis of logs from applications, nodes, and clusters, allowing for advanced searches and detailed log visualizations.

To install and integrate the **ELK Stack** with Kubernetes, **Helm** is often used to simplify the process.

**1. Add the Helm Repository for Elasticsearch**

After installing Helm, the first step is to add the **Helm repository** for Elasticsearch:

```bash
helm repo add elastic https://helm.elastic.co
helm repo update
```

This command adds the Elasticsearch Helm chart repository.

**2. Install Elasticsearch**

You can install Elasticsearch using the official Helm chart with the following command:

```bash
helm install elasticsearch elastic/elasticsearch
```

This command will create an Elasticsearch instance in the cluster, ready to handle log indexing and searching.

**3. Install Kibana**

To use **Kibana**, it must be installed separately:

```bash
helm install kibana elastic/kibana
```

This command installs Kibana on your Kubernetes cluster, which will automatically connect to the Elasticsearch instance installed earlier.

**4. Install a Log Shipper (Fluentd or Filebeat)**

To send the logs generated by pods and containers to Elasticsearch, a **log shipper** such as **Fluentd** or **Filebeat** is required. These tools collect logs from Kubernetes nodes and send them to Elasticsearch for analysis.

To install **Filebeat**, use the following command:

```bash
helm install filebeat elastic/filebeat
```

**Filebeat** is lightweight and easy to configure. Once installed, Filebeat will automatically link container and pod logs to Elasticsearch.

**5. Verify Installation Status**

You can verify if all components are installed correctly by running:

```bash
kubectl get pods
```

Ensure that all pods for Elasticsearch, Kibana, and Filebeat are running without errors.

**6. Access Kibana**

Once Kibana is installed, you can access its interface. However, since Kibana is exposed only within the cluster, you need to use port forwarding to access it locally:

```bash
kubectl port-forward svc/kibana-kibana 5601
```

This command allows you to access Kibana through your browser at:

```
http://localhost:5601
```

Once in Kibana, configure the **Elasticsearch index** (e.g., `filebeat-*`) to start visualizing the logs collected by Filebeat.

## 7. Security

**Security** is a crucial aspect of managing Kubernetes clusters. The primary reason is that exposed applications and services can become targets of **cyberattacks**. Securing a cluster requires understanding **best practices**, protection techniques, and available tools to mitigate risks.

### 7.1. Pod and Container Security Management

Ensuring the security of pods and containers is essential for maintaining a secure runtime environment. Misconfigurations can compromise the entire cluster's security.

It's important to avoid relying on guides or materials that use **Pod Security Policies** (PSPs), as they were deprecated starting with Kubernetes v1.21 and removed in v1.25. In place of PSPs, Kubernetes introduced a new security mechanism known as **Pod Security Admission** (PSA).

#### 7.1.1. Pod Security Admission

**PSA** was introduced by Kubernetes to simplify the enforcement of pod security policies. It allows different **security modes** to be applied at the **namespace** level, ensuring that pods are created with appropriate security configurations. There are **three security levels** that can be set:

- **Privileged** ${\rightarrow}$ Allows all configurations with no restrictions. Used for applications that require full access to pod capabilities.

- **Baseline** ${\rightarrow}$ Limits potentially risky configurations, such as running as root or using elevated privileges.

- **Restricted** ${\rightarrow}$ Enforces strict restrictions, allowing only configurations that meet stringent security criteria.

PSA must be enabled in the Kubernetes cluster and can be configured using specific **labels** on namespaces.

#### 7.1.2. Best Practices

1. **Use Secure Images** ${\rightarrow}$ Use only verified and scanned container images. Tools like *Clair* or *Trivy* can be used to scan images and ensure they are free of known vulnerabilities.

2. **Utilize PSA** ${\rightarrow}$ With *Pod Security Admission*, you can enforce appropriate security modes at the namespace level, ensuring that only pods meeting specific security criteria can be created. This helps prevent unauthorized pods from running.

3. **Runtime Security** ${\rightarrow}$ Implement tools for runtime security monitoring. Solutions like *Falco* can detect suspicious behavior or anomalous activity within containers, providing an extra layer of protection.

4. **Limit Resources** ${\rightarrow}$ Limiting the resources assigned to pods is a **key best practice** for ensuring both security and efficiency in a cluster. This not only prevents resource exhaustion but also limits the impact of potential DoS attacks. **Resource Requests** and **Limits** are essential tools for managing pod resources. They allow you to set **lower and upper bounds** on the resources a container can use. Specifically, `Requests` indicate the minimum resources a container needs to function, while `Limits` specify the maximum resources a container can use. If the container exceeds this limit, it can be throttled or even terminated. Kubernetes uses this information to schedule pods across cluster nodes based on available resources.

  You can use binary units (kibibytes *Ki*, mebibytes *Mi*, gibibytes *Gi*) or decimal units (kilobytes *kB*, megabytes *MB*, gigabytes *GB*) to specify memory quantities. In the following example, `my-container` requests at least `64 mebibytes` of memory but can never exceed `128 mebibytes`.
  
  For CPU, `milliCPU` is used, representing how many thousandths of a CPU are allocated, such as `500m`, which indicates half a CPU.
  
  Example:

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: my-pod
spec:
  containers:
  - name: my-container
    image: my-image
    resources:
      requests:
        memory: "64Mi"
        cpu: "250m"
      limits:
        memory: "128Mi"
        cpu: "500m"
```

### 7.2. Network Policies

**Traffic control** and **network security** are key aspects of protecting communication between pods within the cluster. **Network Policies** allow you to define rules for network traffic between pods, establishing which pods can communicate with each other and limiting access to exposed services.

Using *Network Policies* helps define which pods can send and receive traffic, both within the cluster and externally. This **reduces the attack surface** and protects **critical services**.

*Network Policies* can also be used to **isolate pods**, limiting unnecessary traffic. This isolation helps prevent the spread of attacks between pods.

It's also a good practice to implement **traffic monitoring tools** to detect abnormal activities. Combining Network Policies with traffic monitoring gives you a clearer view of communications within the cluster, helping to identify potential security issues.

## 8. Kubernetes Dashboard and Interfaces

Kubernetes provides several tools and **interfaces** for interacting with and managing the cluster. Besides `kubectl`, the most commonly used command-line interface, there are various **GUIs** like the `Kubernetes Dashboard`, which simplify cluster management and monitoring by offering a clear view of resources and their statuses.

### 8.1. Kubernetes Dashboard

The **Kubernetes Dashboard** is the official web interface that allows users to manage the cluster visually and intuitively. It's useful for both system administrators and developers who want to monitor and manage resources such as pods, services, deployments, and other Kubernetes entities without using command-line tools.

In addition to visualizing cluster resources, it allows you to create new resources directly from the browser using `yaml` or guided interfaces, and to manage **RBAC** roles and permissions.

### 8.2. Minikube Dashboard

When using Kubernetes through **Minikube**, you can use the `Minikube Dashboard`. It provides a clear visualization of cluster resources, such as pods, deployments, services, and more, making interaction with Kubernetes easier for those who prefer to avoid `kubectl` commands or want a visual overview.

To launch the Minikube Dashboard, simply run:

```bash
minikube dashboard
```

This command launches the dashboard and automatically opens a browser window connected to the local Minikube cluster.

### 8.3. Third-Party Interfaces

#### 8.3.1. Lens

**Lens** is a desktop interface for Kubernetes that offers a comprehensive and visual cluster management experience. It's particularly useful for monitoring multiple clusters simultaneously, viewing resource logs, and accessing advanced metrics. **Lens** provides detailed resource views and integrates a terminal for running `kubectl` commands directly from the interface.

#### 8.3.2. Octant

**Octant** is an **open-source** tool providing an interactive dashboard for Kubernetes, allowing developers to diagnose issues, view cluster resources, and easily access application logs. It offers advanced monitoring features and supports custom plugins to extend its capabilities.

## 9. Best Practices

Deploying Kubernetes in production requires attention to detail not just because of the complexity in configuration but also due to the need to adopt several **best practices** to ensure **security**, **efficiency**, **resilience**, and **maintainability** of applications and the cluster. Below is a list of essential best practices to be aware of.

### 9.1. Security

- **Limit Pod Resources**: Setting CPU and memory limits for containers within pods is a recommended practice to prevent overuse of resources and security issues.

- **Access Management and RBAC**: Use *roles* and *permissions* to limit access for users and processes only to necessary resources.

- **Enable Pod Security Admission**: With *PSA*, you can enforce pod security policies, imposing control levels that limit dangerous configurations.

- **Network Policies**: Use Network Policies to **isolate pods**, restricting unnecessary traffic. This isolation helps prevent attacks from spreading between pods.

### 9.2. Scalability

- **Use Horizontal Pod Autoscaler**: Configuring horizontal pod autoscaling allows the system to automatically adjust the number of pods based on load.

- **Cluster Autoscaler**: Configure the Cluster Autoscaler to add or remove nodes based on workload, optimizing resource usage and reducing costs.

### 9.3. Monitoring & Logging

- **Use Monitoring Systems**: Implement a monitoring system like Prometheus to detect issues in real time and respond quickly.

- **Centralized Logging**: Using logging stacks like ELK to centralize pod-generated logs makes it easier to track errors or anomalies.

### 9.4. Deployment Management

- **Deployment Rolling Updates**: Use *rolling updates* to update applications without downtime, gradually replacing pods with new versions while maintaining a minimum number of active pods.

- **Manage Rollbacks**: Define quick rollback mechanisms for deployments, allowing you to revert to a previous version in case of issues with an update.

### 9.5. Maintenance

- **Eliminate Orphaned Resources**: Periodically check for orphaned resources, such as unused volumes or pods, to avoid resource wastage.

- **Backup Data and Configurations**: Configure automatic backups of persistent volumes and critical cluster configurations to ensure quick recovery in case of issues.

### 9.6. Resource Management

- **Use Namespaces**: Using *namespaces* to isolate different environments (production, staging, development) or teams ensures more organized and secure resource management.

- **Use Labels**: Applying *meaningful labels* to resources helps organize and filter deployments, simplifying monitoring and management.

## 10. Conclusion

In conclusion, Kubernetes offers significant advantages but also presents numerous challenges, primarily related to its complexity and high operational costs. These costs must be carefully evaluated, not only in terms of direct expenses but also in terms of the operational complexity, time, and expertise required.

The economic impact of managing a Kubernetes cluster is typically distributed across a cloud provider, such as *AWS, Google Cloud, or Azure*. In this context, each cluster node and resource generates additional costs that must be continuously monitored and managed to avoid unexpected increases.

Operating a Kubernetes cluster in production demands ongoing attention to daily tasks. Maintenance, regular platform updates, and security policy management require specialized personnel and introduce operational complexity.

To fully leverage Kubernetes, the team must possess specific skills that go beyond those needed for traditional infrastructure management. Training staff to understand both the basics and the advanced features of Kubernetes requires significant investments in both time and resources.

Although Kubernetes is an open-source platform, adopting specific services or features from a cloud provider can lead to **vendor lock-in**. Relying on advanced features from a particular provider can bind the organization to that infrastructure, making it difficult to migrate the cluster or applications to another provider without incurring significant reimplementation costs and migration time.

These challenges must be carefully considered and managed to ensure Kubernetes is the right solution for the organization's specific needs and applications.