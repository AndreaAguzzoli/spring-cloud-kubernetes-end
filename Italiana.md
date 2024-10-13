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

Un Pod può trovarsi in cinque possibili stati:
1. **Pending** ${\rightarrow}$ Il Pod è stato creato ma non è in esecuzione.  
2. **Running** ${\rightarrow}$ Il Pod e i suoi container sono in esecuzione senza problemi.  
3. **Succeeded** ${\rightarrow}$ Il Pod ha completato il ciclo di vita dei suoi container ed è correttamente terminato al termine delle operazioni previste.  
4. **Failed** ${\rightarrow}$ Almeno un container all'interno del Pod è terminato in modo anomalo a seguito di un errore, di conseguenza il Pod è stato terminato.  
5. **Unknown** ${\rightarrow}$ Lo stato del Pod non può essere determinato.

I comandi di `kubectl` permettono di ottenere informazioni sui pod e sul loro stato per una determinata applicazione.

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

### 2.7. Namespaces

In Kubernetes, i **namespaces** offrono un metodo per organizzare e isolare gruppi di risorse all'interno di un singolo cluster. Le risorse in un *namespace* devono avere **nomi unici**, ma lo **stesso nome** può essere utilizzato in **namespace diversi**. I *namespaces* hanno effetto sugli oggetti che appartengono a un namespace, come *Deployment* e *Service*, ma non alle risorse globali di un cluster, come *StorageClass*, *Node* o *PersistentVolume*.

I *namespaces* sono particolarmente utili in ambienti con molti utenti, team o progetti. Se l'obiettivo è gestire un cluster con pochi utenti, probabilmente non sarà necessario creare o gestire i *namespaces*. Tuttavia, man mano che la complessità cresce, i *namespaces* possono fornire funzionalità importanti. È importante ricordare che i namespace non possono essere nidificati e ogni risorsa Kubernetes appartiene solo a un namespace. I namespace possono anche dividere le risorse del cluster tra più utenti utilizzando le *resource quota*.

Per distinguere risorse **leggermente diverse**, ad esempio **versioni** diverse dello stesso software, è preferibile utilizzare le **labels** all'interno dello **stesso namespace**, piuttosto che creare namespace separati.

**Attenzione**: *Per i cluster di produzione, evita di utilizzare il namespace predefinito. Invece, crea namespace dedicati per una migliore organizzazione.*

Kubernetes include **quattro namespace predefiniti**:  
1. **Default** ${\rightarrow}$ Utilizzato di default per qualsiasi nuovo cluster fino a quando non ne viene creato uno nuovo.  
2. **Kube-node-lease** ${\rightarrow}$ Contiene oggetti (chiamati *Lease*) relativi ai nodi. Questi consentono a *Kubelet* di inviare *heartbeat*, in modo che il *Control Plane* possa rilevare eventuali guasti ai nodi.  
3. **Kube-public** ${\rightarrow}$ Questo namespace è **pubblico**, quindi è accessibile da tutti gli utenti, inclusi quelli non autenticati. È riservato alle risorse che devono essere visibili pubblicamente in tutto il cluster.  
4. **Kube-system** ${\rightarrow}$ Riservato agli oggetti creati dal sistema Kubernetes.  

#### 2.7.1 Utilizzo dei Namespace

Quindi i *namespace* isolano le risorse all'interno di uno stesso cluster. Di seguito sono riportate alcune delle principali funzionalità offerte da Kubernetes per la gestione dei *namespace*:

- Visualizzare tutti i namespace presenti nel cluster:
```bash
kubectl get namespaces
```

- Creare un nuovo namespace:
```bash
kubectl create namespace <nome-del-namespace>
```

- Per operare in un namespace specifico, è necessario modificare il contesto corrente:
```bash
kubectl config set-context --current --namespace=<nome-del-namespace>
```

È possibile reperire ulteriori informazioni sui **namespaces** di Kubernetes al link: `https://kubernetes.io/docs/concepts/overview/working-with-objects/namespaces/`

## 3. Deploy e Gestione delle Applicazioni

In *Kubernetes*, il **deployment** di applicazioni è un processo ben organizzato che sfrutta una serie di risorse per gestire lo stato, la scalabilità e l'affidabilità delle applicazioni containerizzate.

Una delle principali funzionalità offerte da Kubernetes è la capacità di gestire automaticamente il ciclo di vita delle applicazioni (*distribuzione iniziale, l'aggiornamento, la scalabilità e il rollback*).

### 3.1. Definizione e Implementazione dei Deployment

Per eseguire il nostro progetto su Kubernetes, dobbiamo creare **risorse Kubernetes**.

In altre parole, si descrive come si desidera che sia il deployment della propria applicazione, e Kubernetes si occupa di determinare i passaggi necessari per raggiungere questo stato.

Le risorse Kubernetes sono definite utilizzando file `.yaml` che vengono poi inviate al cluster attraverso l'interazione con `kubectl`.

Le risorse di cui abbiamo bisogno sono `Deployment` e `Service`.

#### 3.1.1. Deployment

La prima risorsa Kubernetes di cui abbiamo bisogno è un **Deployment**, che viene utilizzato per creare e gestire le istanze dei container (pod).

Un *Deployment* Kubernetes è una risorsa che aiuta a gestire e scalare i pod e le corrispondenti repliche all'interno di un cluster. Automatizza la creazione, la gestione e la scalabilità delle repliche dei pod, garantendo che venga mantenuto lo **stato desiderato** dell'applicazione. Ciò semplifica il processo di distribuzione, aggiornamento e scalabilità delle applicazioni senza la necessità di gestire manualmente l'infrastruttura.

Ad esempio, invece di gestire manualmente le repliche di un'applicazione web, si definisce lo **stato desiderato** (come il numero di repliche o la versione dell'immagine del container) nel Deployment. Kubernetes si occupa di garantire che l'applicazione venga eseguita come specificato, sostituendo automaticamente eventuali repliche non correttamente funzionanti.

I Deployment supportano anche funzionalità come **rollout**, **rollback** e **scaling**, che aiutano a gestire gli aggiornamenti delle applicazioni con tempi minimi di inattività e consentono una di scalare facilmente le risorse in base alla domanda.

Un *Deployment* esegue un'app all'interno del cluster, ma non la rende disponibile ad altre app. Per esporre un'applicazione ad altre, abbiamo bisogno di un **Service**.

#### 3.1.2. Services

Un **Service** rende un pod accessibile agli altri pods e agli utenti al di fuori del cluster. Senza di esso, un pod non è accessibile. Kubernetes assegna ai pods indirizzi IP privati non appena vengono creati nel cluster. Questi indirizzi IP sono **non permanenti**. Se i Pod vengono eliminati o ricreati, ricevono un nuovo indirizzo IP, diverso da quello che avevano prima. Questo è problematico per un client che deve connettersi a un pod.

È qui che entrano in gioco i **Services Kubernetes**. Il *Service* può essere raggiunto facilmente, in qualsiasi momento. Quindi funge da **destinazione stabile** che il client può utilizzare per accedere ad una determinata funzionalità. Il client non deve più preoccuparsi degli indirizzi IP dinamici dei pods.

#### 3.1.3. Best Practice

È una **best practice** memorizzare le risorse correlate alla stessa applicazione all'interno dello **stesso file** `.yaml`. Ciò che faremo è creare un unico file e al suo interno separare le **sezioni Deployment e Service**.

Una seconda **best practice** è raggruppare tutti i file `.yaml` all'interno di una unica directory chiamata `/kube`. Raggruppare tutte le definizioni delle risorse all'interno di una singola directory ci consente di sottomettere tutte le configurazioni al cluster con un unico comando.

```bash
kubectl apply -f kube
```

### 3.2. Gestione dello Stato e degli Aggiornamenti

#### 3.2.1. Self-healing

Il **self-healing** è una delle funzionalità più potenti di Kubernetes.  È progettata per garantire la disponibilità e la resilienza delle applicazioni distribuite. Kubernetes monitora continuamente lo stato delle risorse e intraprende azioni correttive automatiche per mantenere lo stato desiderato del cluster. Sostanzialmente la funzione di **self-healing** riguarda:
- Il **riavvio dei pod e dei container** in caso di **failure**.  
- La **sostituzione** e la **ripianificazione dei pod** in caso di failure di uno o più nodi.  
- Il mantenimento di un corretto **numero di repliche**.
- **Termina i container o i pod** che non rispondono positivamente ai controlli di integrità definiti dall'utente e non li rende disponibili finchè non sono pronti a fornire correttamente il servizio.

Una componente fondamentale per garantire questa funzionalità è **ReplicaSet**. Si tratta di un controller, gestito dal **Controller Manager**, responsabile di garantire che sia sempre in esecuzione un certo numero desiderato di repliche. Se un pod si arresta o fallisce, il *ReplicaSet* lo rileva e crea un nuovo pod per ripristinare il numero desiderato di repliche. Tuttavia, il *ReplicaSet* **non gestisce direttamente la salute interna dei container**, ma si occupa di monitorare solo la presenza dei pod.

Qui entra in gioco **Kubelet**. Si tratta dell'agent che gira su ogni nodo del cluster e si occupa di controllare che i Pods in esecuzione su quel nodo funzionino come previsto. Al contrario di *ReplicaSet* esso verifica la salute interna dei Pods. All'interno delle specifiche di ogni Pod sono definite:
- **Liveness Probe** ${\rightarrow}$ utilizzate da *Kubelet* per verificare la salute e la disponibilità di tale Pod. Se il container fallisce questa verifica, *Kubelet* lo termina e ne avvia uno nuovo.
- **Readiness probe** ${\rightarrow}$ utilizzate per determinare se un container è pronto per ricevere traffico o richieste. Se fallisce questa verifica, *Kubelet* rimuove il container dal servizio, impedendogli di ricevere traffico finché non supera nuovamente il test.

Un'altra componente che supporta questa funzionalità è lo **Scheduler**, responsabile della pianificazione dei pod sui nodi disponibili nel cluster. Quando un nodo diventa non disponibile, lo scheduler ripianifica i pod che erano in esecuzione su quel nodo su altri nodi disponibili nel cluster.

La collaborazione tra queste componenti rende possibile il *self-healing*, consentendo a Kubernetes di ripristinare in autonomia lo stato desiderato del cluster.

Maggiori informazioni sono presenti sul sito ufficiale di Kubernetes: `https://kubernetes.io/docs/concepts/workloads/controllers/replicaset/#how-a-replicaset-works`

#### 3.2.2. AutoScaling

Con Kubernetes è possibile scalare automaticamente le applicazioni per far fronte alle variazioni nella richiesta di risorse. In Kubernetes sono possibili sia l'**horizontal scaling** (incrementare o decrementare il numero di repliche) che il **vertical scaling** (incrementare o decrementare la quantità di risorse destinate ai Pods).

In Kubernetes è possibile effettuare lo scaling in modo **manuale** o **automatico**.

Per quanto riguarda lo **scaling manuale** è possibile effettuare l'*horizontal scaling* mediante la `kubectl CLI`, mentre per il *vertical scaling* bisogna modificare le quantità di risorse assegnate al workload. Maggiori informazioni sullo *scaling manuale* sono disponibili ai link:
- `https://kubernetes.io/docs/tutorials/kubernetes-basics/scale/scale-intro/`
- `https://kubernetes.io/docs/tasks/configure-pod-container/resize-container-resources/`

Fortunatamente Kubernetes fornisce anche funzionalità di **scaling automatico**. 

Per quanto riguarda l'*horizontal scaling* è presente **HPA** (Horizontal Pod Autoscaler). Si tratta di una risorsa implementata come una Kubernetes API e un controller. Essa controlla il numero di repliche, aggiungendo o rimuovendo i pod a seconda del carico di lavoro e di metriche personalizzate. *HPA* monitora continuamente le metriche definite per i pod e sulla base di queste metriche e di altre soglie configurate, calcola il corretto numero di repliche. Se il numero attuale di repliche è diverso dal numero calcolato, *HPA* scala l'applicazione aggiungendo o rimuovendo alcuni pod. Sostanzialmente il suo compito è assicurarsi che le applicazioni possano gestire il carico di lavoro, mantenendo un utilizzo delle risorse ottimale.

Per quanto riguarda il *vertical scaling* è presente **VPA** (Vertical Pod Autoscaler). *VPA* adatta automaticamente le risorse di CPU e memoria richieste dai pod. Esso monitora le risorse utilizzate dai pod e suggerisce modifiche alle risorse richieste. *VPA* può essere configurato per aggiornare i pod esistenti o per applicare modifiche solo ai nuovi pod. Diversamente da *HPA* non è presente di default in Kubernetes ma si tratta di un progetto separato. È possibile trovarlo al seguente link: `https://github.com/kubernetes/autoscaler/tree/9f87b78df0f1d6e142234bb32e8acbd71295585a/vertical-pod-autoscaler`

  
Per gestire carichi di lavoro che devono essere scalati in base alla dimensione del cluster (come il cluster-dns o altri componenti di sistema), puoi utilizzare il **CPA** (Cluster Proportional Autoscaler). Il suo obiettivo è adattare **il numero di nodi** (macchine virtuali o fisiche) nel cluster per garantire che ci siano sufficienti risorse per tutti i pod. *CPA* monitora il cluster per identificare se ci sono pod non schedulabili a causa di risorse insufficienti. Aggiunge nuovi nodi al cluster se non ci sono risorse sufficienti per schedulare i nuovi pod, mentre rimuove i nodi che sono sottoutilizzati. 

Se si preferisce mantenere costante il numero di nodi e scalare verticalmente in base alla dimensione del cluster, è possibile utilizzare **CPVA** (Cluster Proportional Vertical Autoscaler). Questo progetto è attualmente in fase beta e può essere trovato su GitHub: `https://github.com/kubernetes-sigs/cluster-proportional-vertical-autoscaler`

Ulteriori tipologie di scaling e maggiori dettagli sono consultabili al link: `https://kubernetes.io/docs/concepts/workloads/autoscaling/`

#### 3.2.3. Rollout & Rollback

Un meccanismo offerto da Kubernetes che semplifica il deployment delle applicazioni è la possibilità di registrare i *Deployment* e fare **rollback** se necessario. All'interno delle risorse di tipo *Deployment* viene definito lo stato desiderato per l'applicazione, attraverso informazioni come il numero di repliche e il template del Pod.

##### 3.2.3.1. Rollout

Viene definito **rollout** il processo attraverso cui si apportano modifiche all'applicazione. Kubernetes fornisce un modo per eseguire i rollout in modo controllato, permette infatti di definire la `strategy` con cui desideriamo rilasciare l'aggiornamento. Questo influenzerà il modo con cui le modifiche si propagheranno su tutti i Pod che fanno riferimento a quel *Deployment*.

Possiamo controllare il meccanismo di *rollout* della risorsa `Deployment.yaml` utilizzando il campo `strategy` all'interno della sezione `Spec`, come mostrato nel seguente esempio:

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

Ci sono principalmente due strategie di *rollout*:

1. **Recreate** ${\rightarrow}$ Questa strategia ricrea tutte le istanze contemporaneamente. Questo può essere problematico per i servizi in esecuzione che gestiscono traffico live, poiché la ricreazione di tutte le istanze allo stesso tempo provoca **downtime**.

2. **RollingUpdate** ${\rightarrow}$ È il metodo di *rollout* predefinito. Esso adotta un approccio più graduale, invece di ricreare tutti i Pods contemporaneamente, esegue l'aggiornamento su un sottoinsieme di pod, lasciando il resto libero di continuare a gestire il traffico. Inoltre, impostando i parametri `maxSurge` e `maxUnavailable`, possiamo affinare ulteriormente la strategia. 

```yaml
...
strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 2
      maxUnavailable: 0
...
```

Il **rollout** consente quindi anche un **aggiornamento incrementale**. Effettua la sostituzione delle repliche della vecchia versione con quelle della nuova versione, una alla volta o in piccoli lotti, in modo da permettere un funzionamento costante e l'assenza di periodi di indisponbilità delle funzionalità dell'applicazione.

Quello che di solito si fa per effettuare un *rollout* è modificare il file `Deployment.yaml`, in modo da fare riferimento alla nuova versione della applicazione. Successivamente si applicano le modifiche attraverso il comando `kubectl apply` e il rollout inizia. È possibile anche monitorare il rollout per assicurarsi che stia procedendo come previsto attraverso il comando:

```sh
kubectl rollout status deployment/my-deployment
```

##### 3.2.3.2. Rollback

Quando si parla di **rollback** ci si riferisce al processo di ripristino di uno stato stabile precedente della tua applicazione. Questo è utile se un *rollout* introduce problemi e si ha bisogno di tornare rapidamente a uno stato noto e funzionante.

Per eseguire correttamente un rollback si controlla la **cronologia dei rollout** per identificare la versione alla quale di vuole tornare e lo si fa attraverso il comando:

```sh
kubectl rollout history deployment/my-deployment
```

Per eseguire effettivamente il *rollback* si utilizza il comando `kubectl rollout undo`, che così di default ritorna alla versione immediatamente precedente, ma è possibile anche specificare una versione in particolare.

```sh
kubectl rollout undo deployment/my-deployment
```

Essendo che il *rollback* in Kubernetes è una tipologia di *rollout* è possibile monitorarlo attraverso lo stesso comando utilizzato in precedenza:

```sh
kubectl rollout status deployment/my-deployment
```

Utilizzare efficacemente *rollout* e *rollback* permette di gestire gli aggiornamenti delle applicazioni con tempi di inattività minimi, di recuperare rapidamente dai problemi e di assicurarti che le applicazioni rimangano stabili e disponibili durante i cambiamenti.

#### 3.2.4. Service Discovery & Load Balancing

Kubernetes fornisce meccanismi integrati di **service discovery** e **load balancing** tra pod. 

In Kubernetes, il **Service Discovery** è una componente fondamentale per garantire che i microservizi possano trovare e interagire con gli altri microservizi **senza configurazioni manuali**. Kubernetes fornisce ai Pods il proprio indirizzo IP e un nome DNS per i *Services*, in modo da poter bilanciare il traffico tra essi.

##### 3.2.4.1. Service Discovery

È possibile sfruttare il *Service Discovery* di Kubernetes in 2 modi differenti:

1. **DNS-Based** ${\rightarrow}$ Kubernetes configura automaticamente un DNS all'interno del cluster. Ogni servizio creato in Kubernetes ottiene un nome DNS, e i Pods possono comunicare con altri servizi utilizzando questi nomi. Prendiamo per esempio un servizio chiamato `my-service` all'interno del namespace `my-ns`. I Pods all'interno del namespace `my-ns` dovrebbero essere in grado di trovare il servizio semplicemente attraverso il nome `my-service`, mentre quelli all'esterno di quel namespace devono specificare il nome completo `my-service.my-ns`.

2. **Environment Variables** ${\rightarrow}$ Quando un pod viene avviato, Kubernetes attraverso `kubelet` inietta alcune variabili d'ambiente contenenti informazioni sui servizi disponibili. Questo metodo è meno flessibile rispetto al DNS e viene generalmente utilizzato in casi più semplici o legacy. Per esempio per il servizio `my-service` vengono aggiunte le variabili `MY_SERVICE_SERVICE_HOST` e `MY_SERVICE_SERVICE_PORT`, dove il nome del servizio è trasformato in maiuscolo e i trattini vengono sostituiti dagli underscore. 

Sostanzialmente le risorse Kubernetes alla base del *service discovery* sono i *Services*. Per consentire ai vari Pod di comunicare tra di loro è necessario che ogni servizio abbia un file `Service.yaml`, come:

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

Per quello che riguarda il **Load Balancing**, in Kubernetes ci si riferisce alla distribuzione del traffico ai vari Pods di un cluster, in modo da garantire un utilizzo uniforme delle risorse e migliorare la disponibilità e l'affidabilità.

In Kubernetes sono presenti diverse tipologie di *load Balancing*:

1. **Internal Load Balancing** ${\rightarrow}$ Quando viene creato un *Service* di tipo `ClusterIP` (default), viene definito un *load balancer* interno. Il traffico inviato all'indirizzo IP del cluster viene distribuito tra tutti i Pods **in salute** che corrispondono al *selector* di tale servizio.

2. **External Load Balancing**:
  
  2.1. Per esporre servizi ai client esterni e implementare funzioni di *load balancing*, Kubernetes fornisce il tipo di servizio `LoadBalancer`, che interagisce con il cloud provider per creare un bilanciatore di carico esterno per distribuire il traffico in ingresso ai Pods.

  2.2. Un altro modo per esporre un *Service* esternamente è tramite `NodePort`. Questo apre una porta specifica su tutti i nodi, e Kubernetes garantisce che il traffico diretto a quella porta venga distribuito equamente tra i Pods.

3. **Ingress** ${\rightarrow}$ Fornisce un modo per esporre servizi *HTTP* e *HTTPS* all'esterno del cluster e per gestirne il corrispondente traffico. `Ingress` consente di configurare regole avanzate per l'instradamento del traffico a diversi servizi, funzionando come un *load balancer* di livello applicativo.

Per scoprire altre delle numerose funzionalità di Kubernetes si consiglia di visitare il link: `https://kubernetes.io/`

## 4. Configurazione e Gestione di Kubernetes

### 4.1. Installazione Kubernetes

#### 4.1.1. Minikube

Minikube è uno strumento che consente di **creare un cluster Kubernetes localmente**. È particolarmente utile per gli sviluppatori e gli amministratori di sistema che desiderano testare, sviluppare e sperimentare con Kubernetes in un ambiente locale, prima di implementare in un ambiente di produzione. *Minikube* fornisce in ogni caso un ambiente Kubernetes completo e configurabile.

**Prerequisiti**

Oltre ad un po' di memoria libera quello di cui abbiamo bisogno è di un *Container Manager* come **Docker**, oppure di un ambiente di virtualizzazione come *QEMU*, *Hyper-V*, *KVM* o *VirtualBox*. Il motivo è che Minikube lo utilizzerà per creare e gestire la VM su cui gira Kubernetes.

Un'altra cosa di cui abbiamo bisogno è `kubectl` che è lo strumento da riga di comando che si usa per interagire con Kubernetes.

**Installazione** 

Per installare *Minikube* e *kubectl* si consiglia di seguire le guide ufficiali di entrambi disponibili ai link:

- `https://minikube.sigs.k8s.io/docs/start/`

- `https://kubernetes.io/docs/tasks/tools/`

**Caratteristiche principali**

*Minikube* crea un cluster Kubernetes a **nodo singolo**, il che significa che tutto (master e worker) gira su una singola macchina virtuale. Questo è sufficiente per la maggior parte dei casi d'uso di sviluppo e test.

Minikube supporta una serie di addon opzionali che possono essere abilitati per estendere le funzionalità del cluster. Questi addons includono *dashboard*, *metric-server*, *ingress*, e molti altri.

**Vantaggi di Minikube**

1. **Ambiente di test locale**: Ideale per lo sviluppo e la sperimentazione senza dover configurare un cluster completo in un cloud o in un ambiente di produzione.

2. **Velocità e praticità**: Permette di avviare e arrestare rapidamente il cluster, facilitando il ciclo di sviluppo.

3. **Compatibilità**: Supporta la stessa API di Kubernetes, garantendo che il codice scritto e testato su Minikube sia compatibile con i cluster Kubernetes di produzione.

**Avviare Minikube**

Dopo l'installazione per avviare un cluster *Minikube* è necessario eseguire il comando:

```bash
minikube start
```

Per verificare poi che il cluster sia in esecuzione e per controllare lo stato dei nodi è possibile utilizzare i comandi:

```bash
kubectl cluster-info
```

```bash
kubectl get nodes
```

Per eliminare il cluster *Minikube* e la corrispondente VM si esegua il comando:

```bash
minikube delete --all
```

### 4.2. Gestione del Cluster

#### 4.2.1. Gestione dei Nodi

I nodi sono le macchine che eseguono i pod nel cluster Kubernetes. Ecco come gestirli:

- **Visualizzare i nodi:** Per ottenere un elenco dei nodi nel cluster:
```bash
kubectl get nodes
```

- **Dettagli di un nodo specifico:** Per visualizzare informazioni dettagliate, inclusi i suoi stati e le risorse disponibili su un nodo, usa:
```bash
kubectl describe node <nome-del-nodo>
```

- **Aggiungere o rimuovere nodi:** Minikube è progettato per un ambiente di sviluppo, quindi la gestione dei nodi è meno complessa rispetto a un ambiente di produzione. Il modo più comodo per aggiungere più nodi, è avviare *Minikube* con l'opzione per specificare il numero di nodi:
```bash
minikube start --nodes=2
```

#### 4.2.2 Gestione delle Risorse

Kubernetes gestisce diverse risorse all'interno del cluster, come *pods*, *services* e *deployment*. Ecco come visualizzare queste risorse:

- **Pods:**
```bash
kubectl get pods
```
- **Servizi:**
```bash
kubectl get svc
```
- **Deployment:**
```bash
kubectl get deployments
```

- Per ottenere informazioni dettagliate su un pod specifico:
```bash
kubectl describe pod <nome-del-pod>
```

- Per modificare il numero di repliche di un deployment:
```bash
kubectl scale deployment <nome-del-deployment> --replicas=<numero-di-repliche>
```
- Per aggiornare l'immagine di un container in un deployment:
```bash
kubectl set image deployment/<nome-del-deployment> <nome-del-container>=<nuova-imagine>
```

#### 4.2.3 Controllo degli Accessi

Il controllo degli accessi in Kubernetes è un meccanismo che serve per limitare e controllare chi può fare cosa all'interno del cluster. Le principali componenti per gestire il controllo degli accessi in Kubernetes sono 3:

1. **Autenticazione** ${\rightarrow}$ Consiste nel verificare l'identità di chiunque effettui una richiesta al cluster. Kubernetes supporta diversi metodi di autenticazione, tra cui *certificati, token o ID Provider esterni*.

2. **Autorizzazione** ${\rightarrow}$ Si tratta della seconda fase, che segue quella di autenticazione. In questa fase Kubernetes controlla se l'azione che l'utente vuole intraprendere all'interno del cluster è permessa per quel tipo di utente o no. Attraverso Kubernetes si possono utilizzare modelli diversi di autorizzazione:

  - *Role-Based-Access-Control (RBAC)*: consentono di assegnare permessi basati su ruoli a utenti o gruppi. Gli oggetti chiave di **RBAC** sono i *Role* e i *ClusterRole* attraverso i quali vengono definite le autorizzazioni e i *RoleBinding* e *ClusterRoleBinding* che associano le autorizzazioni agli utenti.

  - *Attribute-Based-Access-Control (ABAC)*: questo modello si basa su regole definite dall'amministratore del cluster.

  - *Webhook Authorization*: delega le decisioni sulle autorizzazioni a servizi esterni.

3. **Admission Control** ${\rightarrow}$ Si tratta di plugin eseguiti dopo la fase di *Autorizzazione* e possono modificare o bloccare le richieste. Essi verificano che le richieste soddisfino certi criteri prima di acconsentire.

Il più utilizzato e consigliato tra i modelli di autorizzazione è **RBAC**. Ci sono diversi motivi per cui risulta essere la scelta più gettonata. Il primo su tutti è la **semplicità di gestione**. È infatti più semplice da configurare perchè si basa su ruoli e associazioni chiari e non su attributi manuali. In secondo luogo fornisce una forte **scalabilità**, in quanto in ambienti complessi e con molti utenti è molto versatile grazie alla creazione di ruoli ben definiti. Inoltre *RBAC* è abilitato di default mentre *ABAC* richiede configurazioni manuali, mentre *WA* richiede integrazioni aggiuntive essendo un servizio esterno. Di seguito sono riportati una serie di comandi utili per gestire *RBAC*:

- Visualizzare ruoli e associazioni:
```bash
kubectl get roles -n <namespace>
kubectl get rolebindings -n <namespace>
kubectl get clusterroles
kubectl get clusterrolebindings
```

- Creare un *Role*:
```bash
kubectl create role <role-name> --verb=get,list,watch --resource=pods -n <namespace>
```

- Creare un *ClusterRole*:
```bash
kubectl create clusterrole <role-name> --verb=get,list,watch --resource=pods
```

- Associare un *Role* a un utente o ad un gruppo (*RoleBinding*):
```bash
kubectl create rolebinding <binding-name> --role=<role-name> --user=<user-name> -n <namespace>
```

- Associare un *ClusterRole* a un utente o ad un gruppo (*ClusterRoleBinding*):
```bash
kubectl create clusterrolebinding <binding-name> --clusterrole=<role-name> --user=<user-name>
```

- Eliminare un *Role* o un *Binding*:
```bash
kubectl delete role <role-name> -n <namespace>
kubectl delete rolebinding <binding-name> -n <namespace>
kubectl delete clusterrole <role-name>
kubectl delete clusterrolebinding <binding-name>
```

- Esaminare i permessi di un utente:
```bash
kubectl auth can-i <verb> <resource> -n <namespace> --as=<user>
```

È inoltre possibile creare *Role*, *ClusterRole*, *RoleBinding*, *ClusterRoleBinding* anche attraverso file `.yaml`. Questi consentono una gestione più chiara delle configurazioni di accesso:

Esempio di un file `.yaml` per un *Role*:

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
Applica il Role al cluster con:
```bash
kubectl apply -f role.yaml
```

Esempio di file `.yaml` per un *RoleBinding*:

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

Questi file verranno raccolti come da **Best Practice** all'interno di una unica directory chiamata `/kube` e poi caricati tutti assieme tramite il comando `apply` di  Kubernetes.

Ulteriori informazioni sul *controllo degli accessi* sono reperibili sul sito ufficiale al seguente link: 
`https://kubernetes.io/docs/concepts/security/controlling-access/`


## 5. Storage e Persistenza

Nell’ambiente di Kubernetes, la **gestione dello storage** è un aspetto fondamentale per le applicazioni che necessitano di mantenere informazioni anche dopo il riavvio dei pod o dei nodi. In un ambiente containerizzato, i pod possono essere creati e distrutti frequentemente, rendendo necessaria un’architettura di storage che possa mantenere i dati anche oltre la vita di un singolo pod.

### 5.1. Concetti di Storage

In Kubernetes, il concetto di **storage** è organizzato attorno a diverse risorse e astrazioni. Le applicazioni moderne spesso utilizzano dati **persistenti**, come database e file system condivisi, che richiedono un'archiviazione non dipendente dalla vita del pod. Le possibili risorse di storage di Kubernetes sono:

- **Volumes** ${\rightarrow}$ un *volume* è un'astrazione che rappresenta un'area di storage collegata a un pod. I *volumi* possono essere utilizzati per mantenere i dati generati e utilizzati dai container. Essi sono progettati per essere condivisi tra i container all'interno dello stesso pod e hanno una vita utile che è indipendente da quella del pod stesso. Ciò significa che se un pod viene riavviato, il volume rimarrà intatto e i dati memorizzati saranno ancora disponibili.

- **Persistent Volumes (PV)** ${\rightarrow}$ Un *PV* è una risorsa di storage che è stata creata e configurata nel cluster. I *PV* possono essere configurati da un amministratore e possono rappresentare vari tipi di storage, come dischi locali, storage di rete o storage cloud.

- **Persistent Volume Claims (PVC)** ${\rightarrow}$ Sono richieste di storage da parte degli utenti o delle applicazioni. Quando un utente crea un *PVC*, specifica le caratteristiche richieste, come la quantità di spazio di storage e la modalità di accesso. Kubernetes cerca quindi un *PV* disponibile che soddisfi i requisiti specificati nel *PVC*. Se trova ne trova uno compatibile, lo associa a quel *PVC*, rendendolo pronto per l’uso nei pod.

### 5.2. Ciclo di Vita dello Storage

Il ciclo di vita delle risorse di storage in Kubernetes può essere descritto come segue:

- **Provisioning** ${\rightarrow}$ Questa è la fase in cui vengono **creati i PV**. Può essere effettuato **manualmente** da un amministratore, che configura il PV in base alle esigenze dell'applicazione, oppure **automaticamente** tramite il provisioning dinamico, che crea PV al volo in risposta a PVC.

- **Binding** ${\rightarrow}$ Quando un **PVC** viene creato, Kubernetes esegue un processo di *binding* per cercare un PV che soddisfi le specifiche del PVC. Se un PV compatibile viene trovato, viene associato al PVC, consentendo ai pod di accedervi. Se non esiste un PV compatibile il PVC rimane in stato di *"pending"* fino a che non gli viene fornito un PV valido. Se non si interviene la situazione non cambia, quindi è necessario creare un PV manualmente o abilitare l'utilizzo del provisioning dinamico. 

- **Utilizzo** ${\rightarrow}$ Una volta che un PVC è associato a un PV, i pod possono utilizzare il volume montandolo nel loro filesystem. Questo consente ai container di leggere e scrivere dati in modo persistente, anche se il pod viene riavviato.

- **Reclaiming** ${\rightarrow}$ questa fase determina come viene gestito un PV dopo che il PVC associato è stato rimosso o rilasciato. In altre parole, una volta che il pod non ha più bisogno di accedere ai dati o il claim è stato cancellato. Kubernetes offre 3 possibili politiche di recupero:  
  - *Retain*: Il PV rimane nel cluster ma non è più utilizzabile. Deve essere trattato manualmente per essere riutilizzato.  
  - *Recycle*: Il PV viene cancellato e ripulito automaticamente, pronto per essere riutilizzato da un nuovo PVC.  
  - *Delete*: Il PV viene rimosso insieme ai dati che contiene.  

### 5.3. Importanza dello storage Persistente

La persistenza dei dati è cruciale per molte applicazioni moderne. Senza un'adeguata gestione dello storage, i dati andrebbero persi ogni volta che un pod viene riavviato o ricreato. 

Utilizzare in modo efficace le risorse di storage di Kubernetes permette di costruire applicazioni resilienti, capaci di affrontare guasti e modifiche della configurazione senza perdere dati critici. In un contesto di microservizi e architetture basate su container, questo approccio è fondamentale per garantire la continuità e la disponibilità dei servizi.

## 6. Monitoraggio e Logging

Monitorare lo stato del cluster e delle applicazioni in esecuzione su Kubernetes è essenziale per garantire la stabilità, le prestazioni e la disponibilità del sistema. Il **monitoraggio** e il **logging** aiutano a rilevare problemi, ottimizzare le risorse e identificare eventuali colli di bottiglia o guasti prima che possano diventare critici.

Esistono diversi strumenti che possono essere integrati in Kubernetes per fornire un monitoraggio completo e la raccolta di log.

### 6.1. Strumenti di Monitoraggio

Uno dei primi aspetti da monitorare è lo stato del cluster Kubernetes stesso: risorse come CPU, memoria e disco devono essere costantemente sorvegliate per prevenire sovraccarichi o inefficienze.

Per farlo, si utilizzano strumenti di monitoraggio che raccolgono metriche dalle diverse componenti del cluster, come i nodi, i pod e i container.

Uno degli strumenti più utilizzati per raccogliere e visualizzare metriche dettagliate su Kubernetes è **Prometheus**. Per integrarlo su Kubernetes si può usare **Helm** che è un gestore di pacchetti che ne facilita l'installazione.

#### 6.1.1. Integrazione di Prometheus

L'installazione di **Prometheus** con **Helm** è un modo semplice e standardizzato per configurare il monitoraggio in Kubernetes. Helm è un gestore di pacchetti per Kubernetes che consente di installare applicazioni predefinite (**chart**) in modo rapido e flessibile.

**1. Installare Helm**

Se Helm non è già presente, è necessario installarlo prima di procedere. Puoi seguire le istruzioni ufficiali per installarlo in base al tuo sistema operativo. Per l'installazione vai al link:

`https://helm.sh/docs/intro/install/`

**2. Aggiungere il repository di Helm per Prometheus**

Prima di installare Prometheus, devi aggiungere il repository Helm ufficiale per Prometheus:

```bash
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update
```

Questo comando aggiunge il repository delle **helm charts** di Prometheus e aggiorna l'indice delle chart disponibili.

**3. Installare Prometheus nel cluster**

Per installare Prometheus nel tuo cluster Kubernetes, puoi utilizzare la chart di Helm specifica. Ad esempio, se desideri installare sia **Prometheus** che **Grafana**, puoi farlo con un'unica chart:

```bash
helm install prometheus prometheus-community/kube-prometheus-stack
```

- `prometheus` è il nome che stai assegnando all'installazione di Prometheus.

- `prometheus-community/kube-prometheus-stack` è la chart che include Prometheus, Grafana e tutti i componenti necessari per il monitoraggio.

Questo comando creerà tutti i **Pod**, **Service**, e **ConfigMap** necessari per far funzionare Prometheus e Grafana nel cluster.

**4. Verificare lo stato dell'installazione**

Dopo l'installazione, puoi verificare che Prometheus sia stato installato correttamente eseguendo il seguente comando per vedere i Pod creati:

```bash
kubectl get pods -n default
```

Se hai specificato un altro namespace durante l'installazione, dovrai sostituire `default` con il nome del tuo namespace.

**5. Accedere alla dashboard di Prometheus**

Dopo aver installato Prometheus, è possibile accedere alla dashboard di Prometheus direttamente. Tuttavia, poiché Prometheus è esposto come servizio all'interno del cluster, dovrai eseguire il **port forwarding** per accedere localmente alla dashboard:

```bash
kubectl port-forward svc/prometheus-kube-prometheus-prometheus 9090
```

Questo comando permette di accedere a Prometheus tramite il browser sul tuo computer, aprendo l'URL:

```
http://localhost:9090
```

**6. Accedere a Grafana**

Se hai installato Grafana insieme a Prometheus, puoi accedere a Grafana utilizzando un altro comando di port-forwarding:

```bash
kubectl port-forward svc/prometheus-grafana 3000
```

Puoi quindi aprire Grafana nel tuo browser all'URL:

```
http://localhost:3000
```

Le credenziali di default per Grafana sono:
- **username**: `admin`
- **password**: `prom-operator`

### 6.2. Logging e Tracciamento

Oltre al monitoraggio anche il **logging** è fondamentale per capire il comportamento delle applicazioni in esecuzione nel cluster e tracciare la causa dei problemi. Kubernetes genera grandi quantità di log dai container, dai pod e dagli altri componenti del cluster. Centralizzare e gestire questi log è essenziale per analizzare eventi ed errori.

Si rende spesso necessaria l'integrazione di un sistema di logging nel progetto Kubernetes. Un tipico sistema di logging centralizzato per Kubernetes è basato su una **ELK Stack** (Elasticsearch, Logstash, Kibana) o su **Loki**, una soluzione più recente.

#### 6.2.1. Integrazione di ELK Stack

L'integrazione dell'**ELK Stack** (Elasticsearch, Logstash, Kibana) in Kubernetes è una delle soluzioni più popolari per la raccolta e la visualizzazione dei log in modo centralizzato. L'ELK Stack consente di aggregare e analizzare i log di applicazioni, nodi e cluster, permettendo di eseguire ricerche avanzate e visualizzazioni dettagliate delle informazioni di log.

Per installare e integrare l'**ELK Stack** su Kubernetes si utilizza  **Helm** per semplificare il processo.

**1. Aggiungere il repository Helm per Elasticsearch**

Il primo passo, dopo aver installato Helm, è aggiungere il **repository Helm** per Elasticsearch.

```bash
helm repo add elastic https://helm.elastic.co
helm repo update
```

Questo comando aggiunge il repository delle Helm chart di Elasticsearch.

**2. Installare Elasticsearch**

Puoi installare Elasticsearch utilizzando la chart Helm ufficiale con il seguente comando:

```bash
helm install elasticsearch elastic/elasticsearch
```

Questo comando creerà un'istanza di Elasticsearch nel cluster, che sarà pronta per eseguire.

**3. Installare Kibana**

Per poter utilizzare **Kibana** è necessario installarlo separatamente:

```bash
helm install kibana elastic/kibana
```

Questo comando installerà Kibana nel cluster Kubernetes, che si collegherà automaticamente all'istanza di Elasticsearch installata in precedenza.

**4. Installare un agente di log (Fluentd o Filebeat)**

Per inviare i log generati dai pod e dai container a Elasticsearch, è necessario utilizzare un **log shipper** come **Fluentd** o **Filebeat**. Questi strumenti raccolgono i log dai nodi del cluster Kubernetes e li inviano a Elasticsearch per l'analisi.

Per installare **Filebeat** eseguire il comando:

```bash
helm install filebeat elastic/filebeat
```

**Filebeat** è leggero e facile da configurare. Una volta installato, Filebeat collegherà automaticamente i log dei container e dei pod a Elasticsearch.

**5. Verificare lo stato dell'installazione**

È possibile verificare se tutti i componenti sono stati correttamente installati eseguendo:

```bash
kubectl get pods
```

Bisogna verificare che tutti i pod per Elasticsearch, Kibana e Filebeat siano in esecuzione senza errori.

**6. Accedere a Kibana**

Una volta installato Kibana, è possibile accedere alla sua interfaccia. Tuttavia, poiché Kibana è esposto solo all'interno del cluster, è necessario usare il port forwarding per accedere da locale.

```bash
kubectl port-forward svc/kibana-kibana 5601
```

Questo comando permetterà di accedere a Kibana tramite il browser all'indirizzo:

```
http://localhost:5601
```

Una volta dentro Kibana, configura l'indice di **Elasticsearch** (ad es. `filebeat-*`) per iniziare a visualizzare i log raccolti da Filebeat.

## 7. Sicurezza

La **sicurezza** è un aspetto cruciale nella gestione dei cluster Kubernetes. Il principale motivo è che le applicazioni e i servizi esposti possono essere bersagliati da **attacchi informatici**. Rendere più sicuro un cluster richiede una comprensione delle **best practices**, delle tecniche di protezione e degli strumenti disponibili per la mitigazione dei rischi.

### 7.1. Gestione della Sicurezza dei Pod e dei Container

Garantire la sicurezza dei pod e dei container è essenziale per garantire un ambiente di esecuzione sicuro. Una configurazione errata può compromettere la sicurezza del cluster. 

Un accorgimento importante è quello di evitare di seguire guide o materiale che fa uso delle **Pod Security Policies**. Infatti esse sono state deprecate a partire da Kubernetes v1.21 e sono state rimosse a partire dalla v1.25. A seguito della deprecazione delle PSP, Kubernetes ha introdotto un nuovo meccanismo di sicurezza noto come **Pod Security Admission**.

#### 7.1.1. Pod Security Admission

Le **PSA** sono state introdotte da Kubernetes per semplificare l'applicazione delle politiche di sicurezza per i pod. Esse consentono di applicare diverse **modalità di sicurezza** a livello di **namespace**, garantendo che i pod siano creati con le configurazioni di sicurezza appropriate. Ci sono **tre livelli di sicurezza** che possono essere impostati: 

- **Privileged** ${\rightarrow}$ Consente tutte le configurazioni, senza restrizioni. Utilizzato per applicazioni che richiedono accesso completo alle funzionalità del pod.

- **Baseline** ${\rightarrow}$ Limita l'uso di configurazioni potenzialmente rischiose, come l'esecuzione come root o l'uso di capacità elevate.

- **Restricted** ${\rightarrow}$ Impone restrizioni severe, consentendo solo configurazioni che soddisfano criteri di sicurezza rigorosi.

La PSA deve essere attivata nel cluster Kubernetes e può essere configurata utilizzando determinate **labels** nei namespace.

#### 7.1.2. Best Practices

1. **Utilizzare immagini sicure** ${\rightarrow}$ Utilizza solo immagini container verificate e scansionate per vulnerabilità. Strumenti come *Clair* o *Trivy* possono essere utilizzati per analizzare le immagini e garantire che siano prive di vulnerabilità note.

2. **Utilizzare le PSA** ${\rightarrow}$ Con le *Pod Security Admission*, puoi applicare le modalità di sicurezza appropriate a livello di namespace, assicurandoti che solo i pods che rispettano specifici requisiti possano essere creati. Questo aiuta a prevenire l'esecuzione di pods non autorizzati.

3. **Runtime Security** ${\rightarrow}$ Implementa strumenti per il monitoraggio della sicurezza in fase di esecuzione. Soluzioni come Falco possono rilevare comportamenti sospetti o attività anomale nei container, fornendo un ulteriore livello di protezione.

4. **Limitare le Risorse** ${\rightarrow}$ Limitare le risorse assegnate ai pods è una **best practice fondamentale** per garantire la sicurezza e l'efficienza di un cluster. Questo non solo aiuta a evitare l'esaurimento delle risorse del cluster, ma limita anche l'impatto di eventuali attacchi di tipo DoS. In questa fase **Resource-Request** e **Limits** sono due strumenti fondamentali per la gestione delle risorse nei Pod. Essi danno la possibilità di prefissare un **limite inferiore e superiore** alle risorse che un container può utilizzare. In particolare `Requests` indica la quantità minima di risorse che un container necessita per funzionare, mentre `Limits` specifica la quantità massima di risorse che un container può utilizzare e se supera questo limite può essere limitato o addirittura terminato dal sistema. Kubernetes utilizza queste informazioni per pianificare i Pod sui nodi del cluster in base alle risorse disponibili. 

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

### 7.2. Network Policies

Il **controllo del traffico** e la **sicurezza della rete** sono aspetti fondamentali per proteggere le comunicazioni tra i pod nel cluster. Le **Network Policies** consentono di definire le regole di accesso al traffico di rete tra i pod, stabilendo quali pod possono comunicare tra loro e limitando l'accesso ai servizi esposti.

Utilizzare le *Network Policies* permette di definire quali pod possono inviare e ricevere traffico, sia all'interno del cluster che verso l'esterno. Questo **riduce la superficie di attacco** e protegge i **servizi critici**.

Le *Network Policies* possono essere utilizzate anche per **isolare i pod**, limitando il traffico non necessario. Questo approccio di isolamento aiuta a prevenire la propagazione di attacchi tra pods.

Implementa **strumenti di monitoraggio del traffico** per rilevare attività anomale. La combinazione di Network Policies e monitoraggio del traffico consente di avere una visione più chiara delle comunicazioni all'interno del cluster e di individuare potenziali problemi di sicurezza.

## 8. Kubernetes Dashboard e Interfacce

Kubernetes fornisce diversi strumenti e **interfacce** per interagire e gestire il cluster. Oltre a `kubectl`, l'interfaccia a riga di comando più comune, esistono diverse **GUI** come `Kubernetes Dashboard` che semplificano la gestione e il monitoraggio del cluster, offrendo una visualizzazione chiara delle risorse e degli stati del cluster.

### 8.1. Kubernetes Dashboard

`Kubernetes Dashboard` è un'interfaccia web **ufficiale** che permette agli utenti di gestire il cluster in maniera visiva e intuitiva. È utile sia per gli amministratori di sistema che per gli sviluppatori che vogliono monitorare e gestire risorse come pod, services, deployment e altre entità di Kubernetes, senza dover ricorrere ai comandi da terminale.

Oltre a visualizzare le risorse del cluster permette la creazione di nuove risorse da browser, utilizzando `yaml` o interfacce guidate e la gestione dei ruoli e delle autorizzazioni **RBAC**.

### 8.2. Minikube Dashboard

Se si sta utilizzando Kubernetes attraverso **Minikube** è possibile utilizzare la `Minikube Dashboard`. Essa offre una visualizzazione chiara delle risorse del cluster, come pod, deployment, servizi e molto altro, semplificando l'interazione con Kubernetes per chi preferisce evitare l'uso di comandi `kubectl` o vuole una panoramica visiva. 

Per avviare la Minikube Dashboard è sufficiente eseguire il comando:

```bash
minikube dashboard
```

Questo comando avvia la dashboard e apre automaticamente una finestra del browser connessa al cluster locale Minikube.

### 8.3. Interfacce di Terze Parti

#### 8.3.1. Lens

`Lens` è un'interfaccia desktop per Kubernetes che offre un'esperienza di gestione completa e visiva del cluster. È particolarmente utile per chi vuole monitorare più cluster contemporaneamente, visualizzare i log delle risorse e utilizzare metriche avanzate. `Lens` offre una visualizzazione dettagliata di ogni risorsa, integrando un terminale integrato per eseguire comandi `kubectl` direttamente dall'interfaccia.

#### 8.3.2. Octant

`Octant` è uno strumento **open-source** che fornisce una dashboard interattiva per Kubernetes, permettendo agli sviluppatori di diagnosticare problemi, visualizzare risorse del cluster e accedere facilmente ai log delle applicazioni. Offre funzionalità di monitoraggio avanzate e supporta plugin personalizzati per estendere le sue capacità.

## 9. Best Practices

Implementare Kubernetes in produzione richiede attenzione. Non solo per la complessità nella configurazione, ma anche per l’adozione di alcune **best practice**, al fine di garantire **sicurezza**, **efficienza**, **resilienza** e  **manutenibilità** delle applicazioni e del cluster. Di seguito viene presentato un elenco  delle principali best practice che è bene conoscere.

### 9.1. Sicurezza

- **Limitare le risorse assegnate ai Pod**: Impostare limiti di CPU e memoria per i container all'interno dei pod è una pratica raccomandata per prevenire l'uso eccessivo di risorse e problemi di sicurezza.

- **Gestione degli Accessi e RBAC**: Utilizzare i *roles* e *autorizzazioni* consente di limitare l'accesso a utenti e processi solo alle risorse strettamente necessarie.

- **Abilitare Pod Security Admission**: Con *PSA* è possibile definire politiche di sicurezza per i pod, imponendo livelli di controllo che limitano le configurazioni pericolose.

- **Network Policies**: L'uso  delle Network Policies è consigliato per **isolare i pod**, limitando il traffico non necessario. Questo approccio di isolamento aiuta a prevenire la propagazione di attacchi tra pods.


### 9.2. Scalabilità

- **Utilizzare Horizontal Pod Autoscaler**: Configurare l'autoscaling orizzontale dei pod permette al sistema di scalare automaticamente il numero di pod in base al carico.

- **Cluster Autoscaler**: Configurare il Cluster Autoscaler per aggiungere o rimuovere nodi in base al carico di lavoro. Questo consente di ottimizzare l'utilizzo delle risorse e ridurre i costi.

### 9.3. Monitoraggio & Logging

- **Utilizzare sistemi di monitoraggio**: Implementare un sistema di monitoraggio come Prometheus permette di individuare problemi in tempo reale e poter intervenire rapidamente.

- **Logging centralizzato**: Utilizzare stack di logging come ELK per centralizzare i log generati dai pod permette di tracciare facilmente errori o anomalie.

### 9.4. Gestione Deployment

- **Deployment Rolling Updates**: Utilizzare *rolling updates* per aggiornare le applicazioni senza downtime. Questo meccanismo sostituisce gradualmente i pod con nuove versioni, mantenendo un numero minimo di pod sempre attivi.

- **Gestire i rollback**: Definire meccanismi di rollback rapidi per i deployment, in modo da poter tornare a una versione precedente in caso di problemi con un aggiornamento.

### 9.5. Manutenzione

- **Eliminare risorse orfane**: Verificare periodicamente l'esistenza di risorse orfane, come volumi o pod non utilizzati, per evitare sprechi di risorse.

- **Backup dei dati e delle configurazioni**: Configurare backup automatici di volumi persistenti e delle configurazioni critiche del cluster permette di garantire il ripristino rapido in caso di problemi.

### 9.6. Risorse

- **Utilizzo dei namespace**: Utilizzare i *namespace* per isolare i diversi ambienti (produzione, staging, sviluppo) o i differenti team, garantisce una gestione più ordinata e sicura delle risorse.

- **Utilizzo delle Labels**: applicare *labels significative* alle risorse permette di organizzare e filtrare meglio i deployment e semplificare il monitoraggio e la gestione.

## 10. Conclusione

Possiamo concludere dicendo che Kubernetes offre numerosi vantaggi, ma comporta anche numerose sfide, principalmente legate alla sua complessità e agli elevati costi. I costi andranno attentamente valutati, non solo in termini economici, ma anche in termini di complessità operativa, tempo e competenze necessarie.

Per quanto riguarda l'impatto economico associato alla gestione di un cluster Kubernetes, esso è solitamente distribuito attraverso un cloud provider come *AWS, Google Cloud o Azure*. In questo contesto ogni nodo del cluster e ogni risorsa comporta costi aggiuntivi che vanno attentamente valutati e monitorati costantemente per evitare eccessivi aumenti dei costi.

Gestire un cluster Kubernetes in produzione richiede un'attenzione particolare per le attività quotidiane. La manutenzione, il continuo aggiornamento della piattaforma e la gestione di politiche di sicurezza richiedono personale specializzato e introducono una complessità operativa non trascurabile.

Per sfruttare le potenzialità di Kubernetes, il team che se ne occupa deve avere competenze specifiche che vanno oltre quelle necessarie per la gestione tradizionale dell'infrastruttura. Formare il personale per comprendere oltre ai concetti di base anche funzionalità avanzate richiede investimenti significativi in termini di tempo e risorse.

Anche se Kubernetes è una piattaforma open-source, l'adozione di servizi o funzionalità specifiche di un cloud provider può portare a **vendor lock-in**. L'utilizzo di funzionalità avanzate offerte da uno specifico cloud provider possono vincolare l'azienda a utilizzare una specifica infrastruttura. Questo rende difficile migrare il cluster o le applicazioni verso altri provider senza affrontare significativi costi di reimplementazione e tempo di migrazione.

Queste sfide devono essere attentamente valutate e gestite per assicurarsi che Kubernetes sia la soluzione giusta per le esigenze specifiche dell'organizzazione e delle applicazioni.