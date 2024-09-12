# Kubernetes

## 1. Introduzione

### 1.1. Cos'è Kubernetes?

Kubernetes è una piattaforma **open-source** progettata per automatizzare il deployment, la scalabilità e la gestione di applicazioni containerizzate. Kubernetes è diventato uno **standard de facto** per la gestione dei container grazie alla sua capacità di orchestrazione di container su larga scala, permettendo alle organizzazioni di gestire e scalare applicazioni distribuite in modo efficiente.

Kubernetes è stato sviluppato da Google ed è diventato open-source a partire dal 2014. Quindi nato dall'esperienza di Google per quanto riguarda i sistemi di orchestrazione e containerizzazione è stato donato nel 2015 alla Cloud Native Computing Foundation che ne supporta da allora lo sviluppo e la promozione come standard per la gestione dei container. 

### 1.2. Benefici e Motivazioni

Come abbiamo detto Kubernetes è un **orchestratore di container**. Esso è in grado di scalare su larga scala in modo efficiente e affidabile. Possiamo immaginare un orchestratore di container come un **higly-skilled Tetris player**, containers are the blocks, servers are the boards.

Uno dei principali vantaggi di Kubernetes è la sua capacità di automatizzare molte operazioni, il che migliora l'efficienza e riduce il carico di lavoro. La piattaforma semplifica la scalabilità delle applicazioni, sia aggiungendo ulteriori istanze (scalabilità orizzontale) sia aumentando le risorse delle istanze esistenti (scalabilità verticale). Inoltre, Kubernetes mantiene lo stato desiderato delle applicazioni, garantendo che il numero di repliche e altre configurazioni rimangano coerenti anche in caso di guasti o modifiche.

In aggiunta, Kubernetes offre una notevole portabilità, permettendo alle applicazioni containerizzate di funzionare su qualsiasi tipo di infrastruttura, sia essa basata su cloud o on-premises. La piattaforma facilita anche il processo di aggiornamento delle applicazioni, utilizzando strategie come i rolling updates e i rollback. Questi meccanismi assicurano che le applicazioni possano essere aggiornate senza tempi di inattività significativi e che eventuali problemi possano essere risolti rapidamente.

## 2. Architettura di Kubernetes

Un deployment Kubernetes in funzione è chiamato **Cluster**, cioè un gruppo di host che eseguono container Linux gestiti da Kubernetes. L'architettura di un cluster Kubernetes è basata su un **modello di tipo master-worker**, dove è presente il master, detto **Control Plane Node**, e uno o più nodi di elaborazione, detti **worker nodes**. Questi nodi ospitano i **pod**, che eseguono i carichi di lavoro degli utenti, mentre i nodi del Control Plane gestiscono i worker nodes e coordinano tutte le operazioni all'interno del cluster.

### 2.1. Control Plane

Nel contesto di Kubernetes, il **Control Plane** è responsabile delle decisioni globali sul cluster, come lo scheduling e la risposta agli eventi. I componenti del Control Plane possono essere eseguiti su qualsiasi nodo del cluster, ma spesso, per semplificare la gestione, vengono eseguiti su una macchina separata dagli workload degli utenti. Esistono anche configurazioni **multi-master** per garantire alta disponibilità e failover. Il Control Plane presenta diverse componenti, delle quali, le principali sono:

- **Kube-APIserver** ${\rightarrow}$ È un componente che espone le Kubernetes API. Rappresenta il front-end del Control Plane. Esso è progettato per scalare orizzontalmente, quindi è possibile creare diverse istanze di `Kube-APIserver` e bilanciare il traffico tra esse.  

- **Etcd** ${\rightarrow}$ È un database **key-value** ridondato utilizzato per memorizzare tutte le informazioni del cluster. È essenziale avere una strategia di backup per **etcd** per proteggere i dati.

- **Kube-Scheduler** ${\rightarrow}$ È responsabile dell'assegnazione dei pod ai nodi disponibili. Considera vari fattori come le risorse richieste dai pod, i vincoli hardware, quelli software e le preferenze di affinità per determinare la migliore allocazione.

- **Kube-Controller-Manager** ${\rightarrow}$ Gestisce i controller, che sono responsabili di mantenere lo stato desiderato del cluster. Ad esempio, il *Node Controller* monitora i nodi, il *Replication Controller* mantiene il numero corretto di pod e il *Service Account Controller* gestisce gli account di servizio e i token di accesso.

- **Cloud-Controller-Manager** ${\rightarrow}$ Gestisce le interazioni specifiche con il cloud provider, se il cluster è eseguito su un'infrastruttura cloud. Se il cluster è in un ambiente on-premises, questo componente non è presente. Il *cloud-controller-manager* esegue controllori per gestire le risorse del cloud, come i nodi e i load balancer.

### 2.2. Worker Nodes

I worker nodes in Kubernetes sono i nodi che eseguono i container delle applicazioni. Ogni worker node ospita i pod, che contengono uno o più container, e fornisce l'ambiente di runtime necessario per farli funzionare. I worker nodes comunicano con il Control Plane, che assegna loro i pod da eseguire e gestisce la loro configurazione. I loro principali componenti vengono eseguiti su ogni nodo e sono responsabili dell'esecuzione dei Pods e della gestione del networking. I principali sono:

- **Kubelet** ${\rightarrow}$ Un agente eseguito su ogni nodo del cluster, che assicura che i container siano in esecuzione in un Pod e che i Pods **funzionino correttamente** e siano sani. *Kubelet* non gestisce i container che non sono stati creati da Kubernetes.

- **Kube-proxy** ${\rightarrow}$ È un proxy eseguito su ogni nodo che gestisce le **regole di networking** per i Kubernetes Service. Queste regole permettono la comunicazione tra i nodi del cluster e verso l'esterno.

- **Container Runtime** ${\rightarrow}$ Software responsabile dell'esecuzione dei container. Kubernetes supporta diversi container runtimes (*Docker*, *containerd*, ...).

È possibili trovare ulteriori informazioni sulle componenti e sulla struttura di Kubernetes al link: `- *https://kubernetes.io/it/docs/concepts/overview/components/*`

### 2.3. Addons

In Kubernetes, gli addon sono componenti aggiuntivi che estendono le funzionalità di base del cluster. Questi componenti sono spesso essenziali per l'operatività e la gestione del cluster. Tra gli addons essenziali ci sono il *DNS* per la risoluzione dei nomi all'interno del cluster, la *Dashboard* per la gestione e il troubleshooting del cluster tramite un'interfaccia web, e strumenti per il *monitoraggio* e il *logging* dei container, che permettono di tenere traccia delle metriche e dei log dei container in modo centralizzato. Possono essere inclusi molti *Addons*, alcuni per esempio sono:

- **Networking** ${\rightarrow}$ *Calico*, *Weave Net*

- **Monitoring e Logging** ${\rightarrow}$ *Prometheus*, *Grafana*, *ELK Stack* (*Elasticsearch*, *Logstash*, *Kibana*).

- **Kubernetes Dashboard** ${\rightarrow}$ Una UI web-based per la gestione del cluster Kubernetes.


La maggior parte degli addon può essere installata utilizzando file di configurazione **YAML** che descrivono le risorse Kubernetes necessarie (pod, servizi, deployment, ecc.). I file possono poi essere applicati utilizzando `kubectl apply -f <file>.yaml`, anche se è una **best practice** metterli tutti all'interno della stessa directory `kube` e applicare l'intera cartella `kubectl apply -f kube`.

Se si sta utilizzando **Minikube**, esso offre un modo semplice per abilitare e gestire gli addon tramite il comando `minikube addons` con le sue opzioni:

- Listare gli addon disponibili:
  ```sh
  minikube addons list
  ```

- Abilitare un addon:
  ```sh
  minikube addons enable <addon-name>
  ```

- Disabilitare un addon:
  ```sh
  minikube addons disable <addon-name>
  ```

Ad esempio, per abilitare la **dashboard** di Kubernetes in Minikube, puoi usare:

```sh
minikube addons enable dashboard
```

Gli addon sono un modo potente per estendere e personalizzare un cluster Kubernetes, fornendo funzionalità aggiuntive che aiutano nella gestione, monitoraggio e operatività del cluster.

È possibile trovare informazioni utili e nuovi Addons ai seguenti link: 
- `https://kubernetes.io/docs/concepts/cluster-administration/addons/`
- `https://github.com/kubernetes`

### 2.4. Pod

In Kubernetes, non si parla di container, ma di **Pod**. I **Pod** sono **le più piccole unità di elaborazione** che puoi creare e gestire in Kubernetes. Un Pod è essenzialmente un **contenitore che racchiude uno o più container**, con *risorse di archiviazione e di rete condivise*, e una specifica che definisce come eseguire i container. Il contenuto di un Pod è sempre co-locato e co-schedulato, ed eseguito in un contesto condiviso. Un Pod rappresenta un "host logico" specifico per un'applicazione: contiene uno o più container applicativi che sono strettamente legati. In contesti non cloud, le applicazioni eseguite sulla stessa macchina fisica o virtuale sono analoghe alle applicazioni cloud eseguite sullo stesso host logico.

Oltre ai container applicativi, un Pod può includere *init container*, che vengono eseguiti durante l'avvio del Pod, e puoi anche iniettare *container effimeri* per il debug di un Pod in esecuzione.

Il contesto condiviso di un Pod è un insieme di *namespaces Linux*, *cgroups* e potenzialmente altri aspetti di isolamento, gli stessi che isolano un container. All'interno del contesto di un Pod, le singole applicazioni possono avere ulteriori livelli di isolamento. Sostanzialmente, un Pod è un insieme di container con namespace e volumi di file system condivisi.

In Kubernetes, non si gestiscono mai direttamente i container, ma i Pod che li contengono. Dal punto di vista tecnico, i `Pod` sono risorse come `Deployments` e `Services`.

Spesso un Pod contiene un solo container. In questo caso, si parla di modello **"one-container-per-pod"**, che è l'uso più comune in Kubernetes. In questo caso, possiamo pensare al Pod come ad un wrapper che avvolge un singolo container. Kubernetes gestisce il Pod invece di gestire i container direttamente. In alcuni casi più complessi, è possibile che un Pod contenga più di un container, qui si parla di **"container co-locati"** ma questi container devono lavorare insieme in maniera stretta e condividere le risorse. In questi casi, Kubernetes li tratta come un'unica unità: vengono avviati e arrestati insieme e devono essere eseguiti sullo stesso nodo.

Di solito, non è necessario creare i Pod direttamente, neanche quelli singoli, poiché sono progettati come entità relativamente usa e getta. Quando un Pod viene creato, viene programmato su un nodo del cluster. Il Pod rimane su quel nodo fino a quando l'esecuzione non termina, l'oggetto Pod viene eliminato, il Pod viene sfrattato per mancanza di risorse o il nodo fallisce.

È preferibile creare i Pods utilizzando risorse di workload come i **Deployment**. Ogni Pod è progettato per eseguire una singola istanza di un'applicazione. Se si desidera scalare l'applicazione orizzontalmente, si devono creare più Pod, ciascuno con una propria istanza dell'applicazione. In Kubernetes, questo è chiamato **replicazione**. I Pod replicati vengono solitamente creati e gestiti come un gruppo da una risorsa di workload e dal suo controller.

Per quanto riguarda lo **storage**, un Pod può specificare un insieme di volumi condivisi. Tutti i container all'interno del Pod possono accedere ai volumi condivisi, consentendo la condivisione dei dati. I volumi consentono anche di mantenere i dati in modo persistente in caso di riavvio di uno dei container all'interno del Pod.

Per quanto riguarda il **networking**, ogni Pod ha un indirizzo IP univoco. Tutti i container all'interno di un Pod condividono lo stesso namespace di rete, incluso l'indirizzo IP e le porte di rete. I container all'interno di uno stesso Pod possono comunicare tra loro utilizzando `localhost`. Quando i container di un Pod comunicano con entità esterne, devono coordinarsi nell'utilizzo delle risorse di rete condivise (come le porte). I container che desiderano comunicare con altri in Pods diversi possono farlo tramite la rete IP.

Per impostare i **vincoli di sicurezza** su Pod e container, si utilizza il campo `securityContext` nella specifica del Pod. Questo campo ti offre un controllo granulare su ciò che un Pod o i singoli container possono fare.

Ulteriori informazioni sono presenti nel sito ufficiale di Kubernetes: `https://kubernetes.io/docs/concepts/workloads/pods/`

### 2.5. Services

In Kubernetes, un **Service** è un metodo per esporre un'applicazione di rete che viene eseguita come uno o più Pod nel cluster.

Uno degli obiettivi principali dei *services* in Kubernetes è che non è necessario modificare la propria applicazione esistente per utilizzare un meccanismo di service discovery sconosciuto. Si esegue il codice nei Pod, sia che si tratti di codice progettato per un mondo cloud-native, sia di un'app più vecchia che hai containerizzato, e si utilizza un *service* per rendere quel gruppo di Pods accessibile sulla rete, in modo che i clients possano interagire con esso.

Se utilizzi un **Deployment** per eseguire la tua app, quel deployment può creare e distruggere Pods dinamicamente. Di conseguenza, non sai sempre quanti di quei Pods sono attivi e funzionanti. Kubernetes crea e distrugge i Pods per allinearsi allo stato desiderato del cluster; infatti, I Pod sono risorse effimere. Questo crea un problema: *se alcuni Pod (backends) forniscono funzionalità ad altri Pod (frontends) all'interno del cluster, come fanno i frontends a sapere e tenere traccia dell'indirizzo IP al quale connettersi per utilizzare la parte backend?*

La **Service API** di Kubernetes è un'astrazione che ti aiuta a esporre gruppi di Pods su una rete. Ogni oggetto *Service* definisce un insieme logico di endpoints (di solito questi endpoints sono Pods) insieme a una policy su come rendere quei Pods accessibili.

Sostanzialmente, ponendo che siano presenti più repliche di una certa funzionalità di *backend*, la parte di *frontend* non deve preoccuparsi nè deve sapere a quale Pod o replica si sta connettendo, e se i Pods o le repliche di *backend* dovessero cambiare, deve avvenire i modo trasparente per quanto riguarda il *frontend*. Questo può avvenire grazie all'astrazione fornita dalla *Service API* di Kubernetes, che consente questo disaccoppiamento.

L'insieme dei Pods a cui punta un *Service* di solito viene individuato a partire da un **selector** che viene definito dall'utente. Risulta anche possibile configurare *Service* senza selector.

Se l'applicazione utilizza HTTP, si può usare **Ingress** per controllare il traffico web verso il carico di lavoro. *Ingress* non è un tipo di *Service*, si tratta di una risorsa che gestisce l'accesso dall'esterno verso i *services* interni ad un cluster. *Ingress* funge da punto di ingresso per il cluster, permettendo di consolidare le regole di instradamento e di esporre più componenti del carico di lavoro dietro un unico listener. In sintesi attraverso esso è possibile gestire e configurare l'accesso tramite *HTTP/HTTPS* ai vari *services*, centralizzando e semplificando il controllo del traffico in ingresso.

Concludendo, se una applicazione può utilizzare le API di Kubernetes per la **service discovery**, puoi interrogare l'*API server* per ottenere le **EndpointSlices** corrispondenti, per contattare il *service* desiderato. Kubernetes aggiorna le *EndpointSlices* di un Service ogni volta che cambia l'insieme di Pod nel servizio.

Ulteriori informazioni sui *Services* di Kubernetes sono consultabili sul sito ufficiale di Kubernetes: `https://kubernetes.io/docs/concepts/services-networking/service/`

### 2.6. ConfigMaps & Secrets

Un **ConfigMap** è un API object utilizzato per memorizzare dati non confidenziali in coppie chiave-valore. Una *ConfigMap* è una risorsa utilizzata per gestire configurazioni esterne alle immagini dei container. In altre parole, invece di incorporare configurazioni specifiche (come variabili d'ambiente, file di configurazione o parametri) direttamente nel codice o nell'immagine del container, queste vengono esternalizzate in una *ConfigMap*.

Configurazioni diverse, come le impostazioni di un ambiente di produzione o di sviluppo, possono essere gestite separatamente senza dover modificare l'immagine del container. Questo permette di utilizzare la stessa immagine del container in diversi ambienti, semplicemente cambiando la configurazione tramite la ConfigMap.

Ad esempio, immaginiamo di sviluppare un'applicazione che è possibile eseguire sul proprio computer (per lo sviluppo) e nel cloud (per gestire il traffico reale). Scriviamo il codice in modo che cerchi una variabile d'ambiente chiamata `DATABASE_HOST`. Localmente, impostiamo quella variabile su `localhost`. Nel cloud, la impostimo per fare riferimento a un *Service* che espone il componente DB al cluster. Questo ci consente di eseguire il debug dello stesso codice localmente se necessario, utilizzando la stessa immagine del container in esecuzione nel cloud.

**Attenzione**: *ConfigMap non fornisce segretezza o crittografia. Se i dati che desideri memorizzare sono confidenziali, utilizza un Secret anziché un ConfigMap o strumenti aggiuntivi per mantenere i tuoi dati privati.*

Un **Secret** è un oggetto che contiene una piccola quantità di dati sensibili come una password, un token o una chiave. Tali informazioni potrebbero essere altrimenti incluse in una specifica del Pod o in un'immagine del container. Usare un *Secret* significa che non devi includere dati riservati nel codice della tua applicazione.

Poiché i *Secrets* possono essere creati indipendentemente dai Pods che li utilizzano, c'è meno rischio che il *Secret* (e i suoi dati) venga esposto durante il workflow di creazione, visualizzazione e modifica dei Pods. Kubernetes e le applicazioni che girano nel cluster possono prendere ulteriori precauzioni con i *Secrets*, come evitare di scrivere dati sensibili su memorie non volatili.

I *Secrets* sono simili ai *ConfigMap*, ma progettati specificamente per contenere dati riservati. Possono per esempio essere utilizzati per impostare variabili d'ambiente per un container, fornire credenziali come chiavi SSH o password ai Pods, o consentire al `kubelet` di estrarre immagini dei container da registri privati.

**Attenzione**: *I Secret di Kubernetes vengono archiviati non crittografati per impostazione predefinita nell'archivio dati sottostante dell'API server (etcd). Chiunque abbia accesso all'API può recuperare o modificare un Secret, così come chiunque abbia accesso a etcd. Inoltre, chiunque sia autorizzato a creare un Pod in un namespace può utilizzare tale accesso per leggere qualsiasi Secret in quel namespace.*

Uleriori informazioni su *Secret* e *ConfigMap* sono reperibili sul sito ufficiale di Kubernetes:  
- `https://kubernetes.io/docs/concepts/configuration/configmap/`  
- `https://kubernetes.io/docs/concepts/configuration/secret/`

























## Funzionalità Principali

### AutoScaling

Kubernetes può scalare automaticamente le applicazioni in base al carico di lavoro tramite. Kubernetes fornisce 3 principali meccanismi di autoscaling:

  - **Horizontal Pod Autoscaler (HPA)**: Si tratta di una risorsa, creata quando è abilitato l'autoscaling, controlla la scala di una distribuzione e il numero di repliche, aggiungendo o rimuovendo i pod a seconda del carico di lavoro e di metriche personalizzate. HPA monitora continuamente le metriche definite per i pod e sulla base di queste metriche e soglie configurate, calcola il numero desiderato di repliche. Se il numero attuale di repliche è diverso dal numero calcolato, HPA scala l'applicazione aggiungendo o rimuovendo alcuni pod. L'HPA viene definita tramite oggetti di tipo `HorizontalPodAutoscaler`, che specificano la risorsa da scalare, le metriche da monitorare e le soglie per l'azione di scaling. Sostanzialmente il suo compito è assicurarsi che le applicazioni abbiano abbastanza repliche per gestire il carico di lavoro, mantenendo un utilizzo delle risorse a livelli ottimali.

  - **Vertical Pod Autoscaler (VPA)**:  VPA adatta automaticamente le risorse di CPU e memoria richieste dai pod in esecuzione. Esso monitora le risorse utilizzate dai pod e suggerisce modifiche alle risorse richieste. Può ridimensionare automaticamente le risorse richieste per i pod (in termini di CPU e memoria). VPA può essere configurato per aggiornare i pod esistenti o per applicare modifiche solo ai nuovi pod. Viene definito tramite oggetti di tipo `VerticalPodAutoscaler`.

  - **Cluster Autoscaling (CA)**: Il suo obiettivo è adattare il numero di nodi (macchine virtuali o fisiche) nel cluster per garantire che ci siano sufficienti risorse per tutti i pod. Monitora il cluster per identificare se ci sono pod non schedulabili a causa di risorse insufficienti. Aggiunge nuovi nodi al cluster se non ci sono risorse sufficienti per schedulare i nuovi pod, mentre rimuove i nodi che sono sottoutilizzati, se non sono necessari per soddisfare le richieste dei pod attuali. Tipicamente viene configurato e gestito tramite il **cloud provider** o altre configurazioni specifiche del cluster.

Spesso queste **CA** e **HPA** lavorano assieme. Se il carico di lavoro su un'applicazione aumenta, **HPA** scala il numero di repliche dei pod. In questo caso, se il cluster non ha risorse sufficienti per gestire le nuove repliche dei pod, il **CA** interviene per aggiungere nuovi nodi al cluster.

Invece per quello che riguarda **CA** e **VPA**, esse sono risorse complementari; infatti, **VPA** ottimizza l'utilizzo delle risorse a livello di singolo pod, mentre **CA** ha come focus il cluster nel suo complesso, quindi si assicura che il cluster abbia sufficienti risorse per eseguire tutti i pod.

### Self-healing

Il self-healing è una delle funzionalità più potenti di Kubernetes, progettata per garantire la disponibilità e la resilienza delle applicazioni distribuite. Kubernetes monitora continuamente lo stato delle risorse e intraprende azioni correttive automatiche per mantenere lo stato desiderato del cluster. Le principali componenti che partecipano a questa funzionalità sono:

- **Kubelet**: È l'agent che gira su ogni nodo del cluster e si occupa di monitorare i pod in esecuzione su quel nodo e garantire che siano sempre in esecuzione come previsto. Se un pod smette di funzionare o va in crash, il kubelet tenta di riavviarlo. Se il riavvio fallisce ripetutamente, il pod viene segnalato come non funzionante.

- **Scheduler**: È responsabile della pianificazione dei pod sui nodi disponibili nel cluster. Se un nodo diventa non disponibile, lo scheduler ripianifica i pod che erano in esecuzione su quel nodo su altri nodi disponibili nel cluster.

- **Controller Manager**: È un componente centrale che esegue vari controller che regolano lo stato del cluster. I principali controller che hanno funzioni di self-healing sono `ReplicaSet`, `Deployment` e `StateFulSet`. Essi controllano lo stato dei pod e delle risorse correlate. Se rilevano che il numero delle repliche dei pod non corrisponde a quello atteso creano o rimuovono dei pod per raggiungere lo stato desiderato.

Sostanzialmente la funzione di **self-healing** riguarda il **riavvio dei pod** in caso di failure di una applicazione, la **ripianificazione dei pod** in caso di failure di uno o più nodi e il mantenimento di un corretto numero di **repliche**.

### Rollout & Rollback

Il **rollout** è il processo attraverso cui si apportano modifiche all'applicazione. Kubernetes fornisce un modo per eseguire i rollout in modo controllato, assicurado che la nuova versione sostituisca gradualmente quella vecchia senza tempi di inattività.

Il principale componente che prende parte al rollout è il **Deployment**.  Esso definisce lo stato desiderato per l'applicazione, inclusi il numero di repliche, l'immagine Docker da usare e altre configurazioni. Durante il rollout il **controller Deployment** crea un nuovo **ReplicaSet**per gestire la nuova versione dell'applicazione mentre scala verso il basso il vecchio ReplicaSet.

Il **rollout** consente quindi un **aggiornamento incrementale**. Questa strategia si basa sulla sostituzione delle repliche della vecchia versione con quelle della nuova versione, una alla volta o in piccoli lotti, in modo da permettere un funzionamento costante e l'assenza di periodi di indisponbilità delle funzionalità dell'applicazione.

Per eseguire un rollout è necessario modificare il file `Deployment.yaml`, in modo da fare riferimento alla nuova versione della applicazione. Di solito quello che accade è il riferimento ad una nuova immagine Docker. Successivamente si applicano le modifiche attraverso `kubectl apply` e il rollout inizia. È possibile anche monitorare il rollout per assicurarsi che stia procedendo come previsto attraverso il comando:

```sh
kubectl rollout status deployment/my-deployment
```

Quando si parla di **rollback** ci si riferisce al processo di ripristino di uno stato stabile precedente della tua applicazione. Questo è utile se un rollout introduce problemi e hai bisogno di tornare rapidamente a uno stato noto e funzionante.

Per eseguire correttamente un rollback si controlla la **cronologia dei rollout** per identificare la versione alla quele di vuole tornare e lo si fa attraverso il comando:

```sh
kubectl rollout history deployment/my-deployment
```

Per eseguire effettivamente il rollback si utilizza il comando `kubectl rollout undo`, che così di default ritorna alla versione immediatamente precedente, ma è possibile anche specificare una versione in particolare.

```sh
kubectl rollout undo deployment/my-deployment
```

Essendo che il rollback in Kubernetes è una tipologia di rollout è possibile monitorare anche il rollback attraverso lo stesso comando utilizzato in precedenza per il rollout:

```sh
kubectl rollout status deployment/my-deployment
```

Utilizzare efficacemente i rollout e i rollback ti permette di gestire gli aggiornamenti delle applicazioni con tempi di inattività minimi, di recuperare rapidamente dai problemi e di assicurarti che le applicazioni rimangano stabili e disponibili durante i cambiamenti.

### Service Discovery & Load Balancing

Kubernetes fornisce meccanismi integrati di service discovery e bilanciamento del traffico tra pod. In Kubernetes, il **Service Discovery** è una componente fondamentale per garantire che i microservizi possano trovare e interagire con gli altri microservizi **senza configurazioni manuali**.

È possibile sfruttare il Service Discovery di Kubernetes in 2 modi differenti:

1. **DNS-Based** ${\rightarrow}$ Kubernetes configura automaticamente un DNS all'interno del cluster. Ogni servizio creato in Kubernetes ottiene un nome DNS, e i pod possono comunicare con altri servizi utilizzando questi nomi. Per esempio se un servizio viene chiamato `my-service` nel namespace default sarà raggiungibile tramite `my-service.default.svc.cluster.local`.

2. **Environment Variables** ${\rightarrow}$ Quando un pod viene avviato, Kubernetes inietta alcune variabili d'ambiente contenenti informazioni sui servizi disponibili. Questo metodo è meno flessibile rispetto al DNS e viene generalmente utilizzato in casi più semplici o legacy.

Per consentire ai vari Pod di comunicare tra di loro è necessario che ogni servizio abbia un file `Service.yaml`, come quello mostrato qui:

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

Per quello che riguarda il **Load Balancing**, in Kubernetes ci si riferisce alla distribuzione del traffico in ingresso verso i pod di un'applicazione, in modo da garantire un utilizzo uniforme delle risorse e migliorare la disponibilità e l'affidabilità.

In Kubernetes sono presenti 3 principali tipologie di **Load Balancing**:

1. **Internal Load Balancing** ${\rightarrow}$ Distribuisce il traffico tra i Pod all'interno del cluster. Kubernetes utilizza i `Service` per implementare il bilanciamento del carico interno, attraverso il servizio di tipo `ClusterIP` che distribuisce le richieste tra i Pod selezionati.

2. **External Load Balancing** ${\rightarrow}$ Gestisce il traffico in ingresso proveniente dall'esterno del cluster. Kubernetes fornisce il tipo di servizio `LoadBalancer`, che crea automaticamente un bilanciatore di carico esterno (come un bilanciatore di carico di un cloud provider) per distribuire il traffico in ingresso ai pod.

3. **Ingress** ${\rightarrow}$ Fornisce un modo per esporre servizi HTTP e HTTPS all'esterno del cluster. Consente il bilanciamento del carico, la terminazione SSL e il routing basato su host e percorsi. Richiede un controller Ingress per funzionare (ad esempio, Nginx, Traefik).

### Gestione delle Risorse

La **gestione delle risorse** in Kubernetes è un aspetto cruciale per garantire che le applicazioni funzionino in modo efficiente e affidabile all'interno di un cluster. All'interno di Kubernetes vengono forniti vari strumenti per gestire le risorse assegnate ai container (*CPU, memoria, spazio su disco*). I principali strumenti o meccanismi coinvolti sono:

- **Resource Request & Limits** ${\rightarrow}$ Sono due strumenti fondamentali per la gestione delle risorse nei Pod. Essi danno la possibilità di prefissare un limite inferiore e superiore alle risorse che un container può utilizzare. In particolare `Requests` indica la quantità minima di risorse che un container necessita per funzionare, mentre `Limits` specifica la quantità massima di risorse che un container può utilizzare e se supera questo limite può essere limitato o addirittura terminato dal sistema. Kubernetes utilizza queste informazioni per pianificare i Pod sui nodi del cluster in base alle risorse disponibili. 

  All'interno di questi file è possibile utilizzare unità di misura binarie (kibibyte *Ki*, mebibyte *Mi*, gibibyte *Gi*) o decimali (kilobyte *kB*, megabyte *MB*, gigabyte *GB*) per indicare la quantità di memoria. Per esempio nel caso che verrà mostrato `my-container` richiede almeno `64 mebibyte` di memoria, ma non potrà mai superare i `128 mebibyte`. 
  
  Per quanto riguarda l'unità di misura per la CPU vengono utilizzati i `milliCPU`. Essi indicano quanti millesimi di una CPU vengono utilizzati, se per esempio il **limits** indicato è `500m` ovvero mezza CPU.   
  
  Un esempio:

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

- **Quality of Service (QoS) Classes** ${\rightarrow}$ Servono per determinare la priorità di un Pod rispetto agli altri in termini di allocazione delle risorse e per prendere decisioni in situazioni di stress. Sono possibili 3 classi:
  - **Guaranteed**: Un Pod appartiene alla classe **Guaranteed** se sia le `requests` che i `limits` di tutti i container in un Pod sono uguali.
  - **Burstable**: i `limits` sono superiori alle `requests` per uno o più container.
  - **BestEffort**: il Pod non presenta nè `requests` nè `limits`.

- **Resource Quotas** ${\rightarrow}$ Le **ResourceQuotas** sono utilizzate per limitare la quantità di risorse che possono essere utilizzate dai Pod all'interno di uno specifico **namespace**. Questo è utile in particolare per evitare che singole applicazioni possano monopolizzare le risorse all'interno di un cluster. Di seguito viene mostrato un esempio, all'interno del quale il namespace `my-namespace` può avere al massimo 10 Pod, nelle `requests` viene impostato un massimale di *4 CPU* e di *8Gi* di memoria, mentre per i `limits` il massimo è di *8 CPU* e di *16Gi* di memoria:

```yaml
apiVersion: v1
kind: ResourceQuota
metadata:
  name: my-quota
  namespace: my-namespace
spec:
  hard:
    pods: "10"
    requests.cpu: "4"
    requests.memory: "8Gi"
    limits.cpu: "8"
    limits.memory: "16Gi"
```

- **Limit Ranges** ${\rightarrow}$ Simili a `Requests` e `Limits`, che sono **impostazioni** che si definiscono **a livello di container**, ma a differenza di questi ultimi i `LimitRange` sono oggetti di configurazione che vengono applicati a livello di namespace. Essi servono per impostare i valori minimi, massimi e predefiniti per `requests` e `limits` delle risorse nei container all'interno di quel namespace. Vengono quindi **definiti a livello di namespace** per imporre politiche sulle risorse globali, quindi per tutti i container appartenenti a quel namespace.

  Di seguito viene mostrato un esempio di `LimitRange`:

```yaml
apiVersion: v1
kind: LimitRange
metadata:
  name: my-limit-range
  namespace: my-namespace
spec:
  limits:
  - default:
      cpu: "500m"
      memory: "512Mi"
    defaultRequest:
      cpu: "200m"
      memory: "256Mi"
    max:
      cpu: "1"
      memory: "1Gi"
    min:
      cpu: "100m"
      memory: "128Mi"
    type: Container
```

- **Node Resource Management** ${\rightarrow}$ In Kubernetes è possibile anche la **gestione delle risorse dei nodi**, che serve per assicurare che le risorse fisiche dei nodi del cluster siano allocate ed utilizzate in modo efficiente. Le principali componenti che ne fanno parte sono:

  1. **Scheduler**: Quando un Pod viene creato lo **scheduler** decide su quale nodo del cluster piazzarlo, basandosi su diversi fattori:

  - i limiti sulle risorse imposti sui Pod da `requests` e da `limits`.
  - `Pod Affinity` e `Anti-Affinity`: sono regole per definire preferenze sulla collocazione dei Pod su specifici nodi o lontano da altri Pod.
  - `Taints` e `Tolerations`: Meccanismi che permettono di prevenire la collocazione di Pod su nodi specifici (**Taints**) a meno che non possano tollerarli (**Tolerations**). Di seguito sono mostrati un esempio di `Taints` e uno di `Tolerations`:

```sh
kubectl taint nodes node1 key=value:NoSchedule
```

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: mypod
spec:
  tolerations:
  - key: "key"
    operator: "Equal"
    value: "value"
    effect: "NoSchedule"
```

  - `Resource Availability`: La disponibilità delle risorse sui nodi.

  2. **Eviction**: Si tratta del processo attraverso il quale Kubernetes rimuove i Pod dai nodi per gestire la scarsità di risorse. Quello che va compreso è che quando il sistema si trova in una situazione di **Node Pressure** ovvero nel momento in cui un nodo esaurisce le risorse di memoria o storage, si rende necessaria la rimozione forzata di un Pod. Qui nasce spontanea una domanda: *quale Pod va rimosso?*

    La risposta si basa principalmente sulle **QoS** e sulla **Pod Priority**. I Pods con QoS più bassa (`BestEffort`) sono i primi ad essere rimossi. Mentre i Pods con priorità più alta sono mantenuti sul nodo durante situazioni di scarsità di risorse.

  3. **Resource Quotas** e **Limit Ranges** già spiegati precedentemente.

  4. **Node Affinity**: Permette di definire preferenze per la collocazione dei Pods su nodi specifici basandosi su etichette dei nodi. Di seguito viene mostrato un esempio:

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: mypod
spec:
  affinity:
    nodeAffinity:
      requiredDuringSchedulingIgnoredDuringExecution:
        nodeSelectorTerms:
        - matchExpressions:
          - key: kubernetes.io/e2e-az-name
            operator: In
            values:
            - e2e-az1
            - e2e-az2
```

  5. **Pod Priority & Preemption**: Quando abbiamo parlato di **eviction** abbiamo detto che ai Pods è possibile assegnare una priorità, che determina quali Pods devono essere schedulati e mantenuti in situazioni di scarsa disponibilità di risorse. Quello che non abbiamo detto è che in queste situazioni i Pods con priorità maggiore possono eseguire preemption, ossia rimpiazzare quelli a priorità più bassa. 

    È possibile assegnare queste priorità attraverso una risorsa Kubernetes che prende il nome di `PriorityClass` ed è rappresentata da un file `yaml`. Al suo interno, attraverso il tag `value`, viene definito un valore numerico che rappresenta il valore della priorità. È importante sapere che valori alti corrispondono ad alti valori di priorità. Inoltre attraverso il tag `globalDefault: true` è possibile definire una classe come priorità di default per tutti i Pods per cui non è stata specificata una `PriorityClass`, se invece viene settato a `false` allora non è quella di default.

    Di seguito viene mostrato un esempio di `PriorityClass` e poi di un `Pod` che la utilizza per specificare un livello di priorità:

```yaml
apiVersion: scheduling.k8s.io/v1
kind: PriorityClass
metadata:
  name: high-priority
value: 1000
globalDefault: false
description: "This priority class should be used for XYZ service pods only."
```

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: high-priority-pod
spec:
  priorityClassName: high-priority
  containers:
  - name: mycontainer
    image: myimage
    resources:
      requests:
        memory: "64Mi"
        cpu: "250m"
      limits:
        memory: "128Mi"
        cpu: "500m"
```

## Conclusione

Kubernetes offre numerosi **vantaggi** che lo rendono una scelta popolare nella gestione di container e microservizi:

- **Autoscaling**: Kubernetes può automaticamente **scalare orizzontalmente** (aggiungendo più istanze di un'applicazione) e **verticalmente** (aumentando le risorse di una singola istanza).

- **Portabilità**: Kubernetes funziona su diversi ambienti, inclusi cloud pubblici, privati e on-premises, facilitando il movimento delle applicazioni tra ambienti diversi senza sostanziali modifiche.

- **Affidabilità e Resilienza**: Grazie alle funzionalità di **self-healing** e di **ReplicaSet** monitora costantemente lo stato dei Pods e li rimpiazza automaticamente in caso di fallimento, garantendo così che un numero specifico di istanze sia costantemente in esecuzione.

- **Gestione delle ConfigMaps e dei Secrets**: Fornisce un modo per gestire la configurazione dell'applicazione e le informazioni sensibili (come password e chiavi API) in modo sicuro e separato dal codice dell'applicazione.

- **Service Discovery & Load Balancing**: L'utilizzo di un DNS interno per permettere ai Pods di trovare e comunicare con gli altri servizi senza necessità di configurazioni manuali, inoltre il traffico viene automaticamente distribuito tra i Pods per garantire un corretto e uniforme utilizzo delle risorse.

- **Rollout & Rollback semplificati**: Attraverso il `rollout` permette di aggiornare le applicazioni senza tempi di inattività, sostituendo gradualmente i pod con la nuova versione. Inoltre facilita il ritorno a una versione precedente dell'applicazione in caso di problemi.

- **Gestione delle risorse**: permette di specificare le risorse minime e massime da utilizzare sia a livello di Pods e sia a livello di namespace.

- **Isolamento e Sicurezza**: I `namespaces` forniscono un meccanismo per isolare e gestire risorse e accessi a livello di namespace, inoltre è possibile anche definire **network policies** per controllare il traffico tra Pods e Services, migliorando la sicurezza delle comunicazioni interne al cluster.

- **Monitoring e Logging**: Supporta l'integrazione con strumenti di monitoring e logging come `Prometheus`, `Grafana`, e `ELK stack`, fornendo una visibilità completa sulle performance e lo stato delle applicazioni.

Nonostante i numerosissimi vantaggi, Kubernetes presenta anche alcuni svantaggi, dei quali i principali sono:

- **Complessità**: Kubernetes può essere **complesso**; infatti presenta una curva di apprendimento molto ripida a causa della sua complessità e delle numerose funzionalità avanzate. In particolare possono creare particolari problemi la **configurazione iniziale** e **gestire le risorse** in modo corretto ed efficiente.

- **Overhead operativo e Costi**: La configurazione iniziale spesso molto complessa può richiedere alti costi in termini di risorse e tempo, inoltre la manutenzione e gli aggiornamenti possono essere complessi e richiedere ulteriore tempo e risorse, specialmente in ambienti di produzione su larga scala. Non è da sottovalutare la necessità di una continua gestione del cluster che può richiedere anche personale aggiuntivo.

- **Risorse Hardware**: Kubernetes può avere un overhead significativo in termini di risorse hardware, richiedendo una quantità considerevole di CPU, memoria e storage per il funzionamento del cluster stesso. Inoltre per applicazioni o ambienti di piccole dimensioni, Kubernetes può risultare sovradimensionato e inefficiente.

- **Sicurezza**: La sicurezza di Kubernetes richiede configurazioni specifiche e attente, e le impostazioni di default potrebbero non essere sufficienti per tutti gli ambienti. Inoltre la complessità del sistema aumenta la superficie di attacco e può introdurre vulnerabilità se non configurato e gestito correttamente.

Possiamo concludere dicendo che Kubernetes offre numerosi vantaggi, ma comporta anche numerose sfide, principalmente legate alla sua complessità e agli elevati costi. Queste sfide devono essere attentamente valutate e gestite per assicurarsi che Kubernetes sia la soluzione giusta per le esigenze specifiche dell'organizzazione e delle applicazioni















# Minikube

Minikube è uno strumento che consente di **eseguire un cluster Kubernetes localmente**. È particolarmente utile per gli sviluppatori e gli amministratori di sistema che desiderano testare, sviluppare e sperimentare con Kubernetes in un ambiente locale, che rispecchia fedelmente l'ambiente di produzione Kubernetes, prima di implementare in un ambiente di produzione.


## What you'll need

Oltre ad un po' di memoria libera quello di cui abbiamo bisogno è di un Container o VM Manager come Docker, QEMU, Hyper-V, KVM, VirtualBox o altri. Il motivo è che Minikube lo utilizzerà per creare e gestire la VM su cui gira Kubernetes.

## Caratteristiche principali di Minikube:

1. **Cluster a nodo singolo**: Minikube crea un cluster Kubernetes a nodo singolo, il che significa che tutto (master e worker) gira su una singola macchina virtuale (VM). Questo è sufficiente per la maggior parte dei casi d'uso di sviluppo e test.

2. **Supporto per i driver di virtualizzazione**: Minikube può utilizzare diversi driver di virtualizzazione, come VirtualBox, Hyper-V, Docker, e altri, per creare e gestire la macchina virtuale in cui gira Kubernetes.

3. **Componenti Kubernetes**: Minikube include tutti i componenti essenziali di Kubernetes come kubeadm, kubelet, e kubectl. È possibile interagire con il cluster tramite kubectl, lo strumento di linea di comando standard di Kubernetes.

4. **Addons**: Minikube supporta una serie di addon opzionali che possono essere abilitati per estendere le funzionalità del cluster. Questi addon includono dashboard, metric-server, ingress, e molti altri. Un **addon** è una funzionalità aggiuntiva che può essere abilitata o disabilitata per estendere le capacità di un cluster. Essi racchiudono funzionalità aggiuntive che possono semplificare lo sviluppo e la gestione delle applicazioni all'interno di un ambiente Kubernetes locale.

## Vantaggi di Minikube:

- **Ambiente di test locale**: Ideale per lo sviluppo e la sperimentazione senza dover configurare un cluster completo in un cloud o in un ambiente di produzione.

- **Velocità e praticità**: Permette di avviare e arrestare rapidamente il cluster, facilitando il ciclo di sviluppo.

- **Compatibilità**: Supporta la stessa API di Kubernetes, garantendo che il codice scritto e testato su Minikube sia compatibile con i cluster Kubernetes di produzione.

## Installation

**https://minikube.sigs.k8s.io/docs/start/**


# First Example

In questo esempio utilizzeremo Kubernetes attraverso Minikube. Faremo utilizzare Docker a Minikube per la creazione e la gestione del container sul quale farà girare Kubernetes. Utilizzeremo l'esempio **spring-cloud-discovery-end** che si trova in *learn-microservices*.



 Esempio di utilizzo di Minikube:

1. **Avvio del cluster**:
   ```sh
   minikube start
   ```

2. **Interazione con il cluster**:
   Una volta che il cluster è in esecuzione, è possibile utilizzare `kubectl` per interagire con esso. Ad esempio, per ottenere l'elenco dei pod:
   ```sh
   kubectl get pods -A
   ```