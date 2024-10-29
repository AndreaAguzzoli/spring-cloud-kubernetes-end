# Riassunto di Spring Cloud Kubernetes

## 1. Introduzione

K8S è una **piattaforma open-source** ideata per automatizzare il **deployment**, la **scalabilità** e la **gestione** di applicazioni containerizzate. Oggi è diventato lo **standard de facto** per la gestione dei container.

Le principali caratteristiche che lo hanno reso tale sono la capacità di **scalare le applicazioni** in modo rapido ed efficiente, l'automatizzazione delle operazioni, la capacità di **mantenere uno stato desiderato** anche in caso di errori, **portabilità** garantita anche su infrastrutture cloud o on-premises e facilità di update tramite **rolling updates e rollback**.

## Architettura

Un **Cluster K8S** è un gruppo di host che eseguono container Linux. L'architettura di un cluster è basata su un modello di tipo **master-worker** dove il master è il **control-plane-node** che gestisce e coordina i **worker nodes** sono uno o più nodi di elaborazione dove vengono ospitati i **pods**.  

### 2.1. CP

Le principali componenti del control-plane sono:
- **Kube-APIserver**: è un front-end per le API di Kubernetes
- **etcd**: database contenente le info e le configurazioni del cluster
- **Kube-Scheduler**: assegna i pods ai nodi
- **Kube-Controller-Manager**: gestione dei controller, responsabili di mantenere uno stato corretto e lo stato desiderato
- **Cloud-Controller-Manager**: gestisce le interazioni con i cloud provider

### 2.2. Worker
Il **CP** assegna i pods ai **worker nodes** che sono responsabili di eseguire i container delle applicazioni. Infatti ogni worker ospita i pods che contengono uno o più container e forniscono l'ambiente di runtime per farli funzionare. Principali componenti:

- **Kubelet** agente eseguito su ogni nodo ch verifica l’esecuzione dei pod
- **Kube-proxy** proxy eseguito su ogni nodo che gestisce regole di networking
- **Container Runtime** sw responsabile dell'esecuzione dei container

### 2.3. Addons

Estendono le funzionalità del cluster con componenti aggiuntivi come DNS, dashboard di gestione e strumenti di monitoraggio e logging.

La maggior parte degli **addons** può essere installate attraverso file di configurazione `.yaml`

Vengono poi mostrati i principali comandi per la gestione degli addons in **Minikube** con i principali comandi.

### 2.4 PODS

In K8S l'elemento base non è il container ma il **POD**. Esso è la più piccola unità che è possibile creare e gestire. Ogni POD è un contenitore che racchiude uno o più container, nel caso siano presenti più container essi hanno risorse di rete e di archiviazione condivise.

Principalmente in un POD possiamo trovare container applicativi che sono i responsabili dell'esecuzione delle applicazioni, ma possiamo trovare anche *init container* che si eseguono all'avvio del Pod e *container effimeri* utilizzati per il debug temporaneo.

In K8S si gestiscono i Pod non i singoli container e lo si fa attraverso risorse come i Deployment che facilitano scalabilità e replicazione. Se si vuole per esempio aumentare le istanze di una applicazione è sufficiente creare più Pod identici, ognuno con una sua copia dell'applicazione così da distribuire il carico.

Ogni Pod ha un proprio IP univoco. I container interni al pod comunicano tramite `localhost` mentre quelli esterni comunicano attraverso la rete condivisa.

Sono poi elencati i possibili stati in cui può trovarsi un Pod.