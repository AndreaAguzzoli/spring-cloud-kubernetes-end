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

### 2.5 Services

In K8S un **Service** è un modo per esporre una applicazione di rete che viene poi eseguita come uno o più pod nel cluster.

Il concetto è che si esegue il codice dell'applicazione nei pods e poi si utilizza un **service** per rendere quel gruppo accessibile sulla rete in modo che i client possano interagire con esso.

In K8S i pod sono risorse effimere e vengano continuamente creati e distrutti, questo rende difficile il caso in cui una serie di pods forniscono funzionalità ad altri pods, come fanno i frontend a sapere a quale IP connettersi per comunicare con il backend?

I Service definiscono quindi una **astrazione** che espone un insieme logico di endpoints e una policy su come rendere accessibili quei pods.

### 2.6 ConfigMaps e Secrets

Una **configMap** è un oggetto utilizzato per memorizzare dati non confidenziali in coppia chiave-valore. Si tratta di risorse utilizzate per gestire configurazioni esterne, in altre parole invece di incorporare configurazioni specifiche all'interno del codice o nell'immagine del container, queste vengono esternalizzate in una ConfigMap. (per esempio impostazioni diverse per ambiente di produzione o di sviluppo)

Un **Secret** è un oggetto che contiene una piccola quantità di dati sensibili come un token o una password. Usare un Secret significa non includere dati riservati all'interno del codice della propria applicazione. Essi hanno un ciclo di vita indipendente rispetto ai pods che li utilizzano, questo fa si che diminuisca il rischio che vengano esposti dati sensibili in fase di creazione, visualizzazione o modifica dei pods.

**Attenzione**: ConfigMap non fornisce segretezza o crittografia, quindi non è adatta a contenere dati sensibili. I Secrets sono memorizzati per default in modo non cifrato su etcd, quindi chiunque abbia accesso all'API o a etcd può recuperare o modificare un Secret, come chiunque abbia i permessi per creare un nuovo Pod in un namespace può leggere qualsiasi secret in quel namespace.

### 2.7 Namespaces

I namespaces offrono un modo per organizzare e isolare gruppi di risorse all'interno di un cluster. Le risorse all'interno dello stesso namespace devono avere nomi univoci, ma lo stesso nome può essere utilizzato in namespace diversi. Essi risultano particolarmente utili in ambienti con un elevato numero di utenti o progetti. Se non si ha una situazione come questa probabilmente essi non sono necessari, ma al crescere della complessità possono fornire funzionalità importanti.

## 3. Deploy e Gestione delle applicazioni

Sostanzialmente quello che avviene in K8S è che attraverso le risorse che creiamo viene definito lo stato che si desidera per la propria applicazione e K8S si occupa di generare una serie di passaggi necessari per raggiungere quello stato.

Le 2 principali risorse di cui abbiamo bisogno sono **Service** e **Deployment**.

I Deloyment sono la prima risorsa di cui abbiamo bisogno che ci serve per gestire le istanze dei container della nostra applicazione. Ci aiuta a creare, gestire e scalare i nostri Pods e le relative repliche. Definisce uno stato desiderato che poi K8S manterrà attivo senza necessità di interventi manuali. Il deployment tuttavia non definisce come esporre i pods per fare questo abbiamo bisogno dei service.

Come abbiamo già detto prima i Service si usano per esporre i Pod sulla rete e forniscono una destinazione stabile che i client possono utilizzare per accedere ad una determinata funzionalità.

Solitamente è una best practice memorizzare le risorse correlate alla stessa applicazione nello stesso file `.yaml`, quindi creare un unico file con una parte di deployment e una di service, ma non è obbligatorio. Una best practice importante è di unire tutti i file `.yaml` in un'unica directory chiamata `/kube` con cui andremo applicare tutto in una volta sola e non un file alla volta.

### Self-healing

Una delle funzionalità più potenti di K8S è il **self-healing** ovvero la capacità di intraprendere in modo autonomo azioni correttive per mantenere lo stato desiderato. Viene fatto attraverso una serie di azioni, come il riavvio, la sostituzione o la ripianificazione dei pod in caso di failure, il mantenimento di un corretto numero di repliche e terminando e sostituendo i pod che non rispondono positivamente ai controlli di integrità. 

è presente poi una descrizione più dettagliata sui controller responsabili di queste azioni e su come avvengono i controlli di integrità.

### Autoscaling

Viene poi presentata un'altra importante funzionalità di K8S ovvero l'autoscaling. In K8S è possibile sia lo scaling orizzontale, incrementando o decrementando il numero di repliche e sia lo scaling verticolare incrementando o decrementando la quantità di risorse destinata ai Pod.

In K8S questo avviene in modo automatico, ma è possibile anche una interazione manuale e sono presenti un paio di link su cui reperire maggiori informazioni.

Sono poi presenti informazioni sui controller responsabili dello scaling orizzontale e verticale e sul Cluster Proportional Autoscaler che incrementa il numero di nodi se non ci sono risorse sufficienti per i nuovi Pod ed elimina invece quelli sotto utilizzati.

### Rollout e Rollback

Viene definito rollout il processo con cui si apportano modifiche all'applicazione. K8S permette di farlo in modo controllato e di definire la strategia con cui si vuole che le modifiche si propaghino all'interno dell'applicazione. Può essere utilizzata la strategia **recreate** che ricrea contemporaneamente tutte le istanze, può essere problematico perchè crea downtime. Un'altra strategia è **RollingUpdate** che è il metodo di default ed è più graduale operando a mano a mano su sottoinsiemi di pods.

È presente un esempio per capire meglio di cosa si sta parlando.

È possibile anche il **rollback** che è particolarmente utile nel caso in cui uno o più aggiornamenti causino un comportamento imprevisto. Consente di tornare in modo rapido ad uno stato funzionante. Avviene sostanzialmente come su github, si sceglie nella cronologia dei rollout a quale stato tornare e ci si torna. Sono poi presenti tutti i comandi necessari per eseguirlo correttamente.

### Service Discovery e Load Balancing

K8S fornisce meccanismi integrati di load balancing e di service discovery che non rendono necessarie configurazioni manuali. Il service discovery può essere **DNS-based** o **Environment-variables based**. 

In K8S viene definito un DNS interno al cluster e ad ogni service viene dato un nome DNS e i pod possono comunicare tra loro utilizzando direttamente questi nomi all'interno del cluster.

Quando viene avviato un Pod vengono iniettate una serie di variabili d'ambiente contenenti info sui servizi disponibili come indirizzo e numero di porta. Viene utilizzato in casi più semplici o legacy. Nel nostro caso abbiamo appunto utilizzato questa tecnica per semplicità.

Quando si parla invece di **load balancing** si parla della distribuzione uniforme del traffico ai vari pod di un cluster per garantire disponibilità e affidabilità. Questo può essere fatto in diversi modi a seconda del caso d'uso.

- Se si crea un **clusterIP** è di tipo **INTERNAL**, in questo caso il traffico viene mandato tutto all'IP del cluster e distribuito in modo equo ai pods in salute. 

- Se è **EXTERNAL** si può creare un tipo di service **loadBalancer** che interagisce con il cloud provider per creare un bilanciatore esterno. Oppure si può usare il tipo **NodePort** che apre una porta specifica su tutti i nodi e garantisce che il traffico venga smistato in modo equo tra i pods su quella porta.

- Oppure è possibile utilizzare **INGRESS** che fornisce un modo per esporre servizi su HTTP e HTTPS e consente di definire regole di instradamento del traffico avanzate, consentendo se si vuole anche di bilanciare il traffico creando un load balancer di livello applicativo.

## 4. Minikube

Minikube è uno strumento che consente di creare un cluster K8S in ambiente locale e risulta particolarmente utile per gli sviluppatori che desiderano testare e sviluppare in ambiente locale prima di spostare in produzione. Minikube fornisce in ogni caso un ambiente K8S completo e configurabile. Esso ha bisogno di un Container Manager come Docker o di un ambiente di virtualizzazione come per esempio KVM per funzionare, il motivo è che lo utilizzerà per creare e gestire la VM su cui gira l'ambiente locale K8S.

In questo capitolo è presente una piccola presentazione di Minikube e una guida all'installazione e all'uso con link che rimandano alla documentazione ufficiale per l'installazione e i principali comandi da usare per un primo approccio efficace.

### 4.2.3 Controllo degli accessi

Mi soffermo un attimo su un concetto presente in questo capitolo che è il controllo degli accessi che permette di decidere chi può fare cosa e di limitarlo. In K8S questo può essere fatto in 3 modi:

- **Autenticazione**: consiste appunto nel verificare l'identità di chiunque effettui una richiesta al cluster. K8S supporta diversi metodi di autenticazione

- **Autorizzazione**: È la fase dopo l'autenticazione, in cui si controlla se quel tipo di utente può svolgere quel tipo di azione. Ci sono diversi tipi di questo meccanismo e il più usato è RBAC dove sono presenti *Role* e *ClusterRole* che definiscono le autorizzazione e i *RoleBinding* e i *ClusterRoleBinding* che associano le autorizzazioni agli utenti. È il più utilizzato perchè anche in casi complessi definisce un modo chiaro e semplice di definizione delle autorizzazioni.

- **Admission Control**: è una eventuale terza fase dopo quella di autorizzazione che verifica attraverso dei plugin se modificare o bloccare le richieste in base a se soddisfano certi criteri.

È poi presente un esempio abbastanza dettagliato su quali sono i comandi e come sono fatti i file `.yaml` per la gestione dei ruoli in K8S.

## 5. Storage

I dati presenti all'interno dei container sono effimeri e questo rappresenta un problema per quelle applicazioni che necessitano di mantenere i dati. Infatti hanno un ciclo di vita dipendente da quello del container stesso. Per questo in Kubernetes sono stati introdotti i **Volumi** che sono una risorsa correlata al Pod e che ha un ciclo di vita direttamente collegato a quello del pod stesso. Tuttavia ha un ciclo di vita che non dipende da quello dei container, essi possono essere eliminati, riavviati o ditrutti ma i dati non verranno eliminati. Questo è particolarmente utile per mantenere dati temporanei all'interno di un Pod o per la condivisione di dati tra container all'interno dello stesso Pod.

Un altro concetto di storage importante sono i **Persistent Volumes** che hanno un ciclo di vita indipendente dal ciclo di vita del Pod e sono utili per quelle applicazioni che necessitano di mantenere una persistenza dei dati anche oltre il ciclo di vita dei Pod.

Un altro concetto sono le **Persistent Volumes Claims** che sono richieste di storage da parte di utenti e applicazioni. Nelle richieste sono presenti le caratteristiche richieste e Kubernetes cerca quindi un PV disponibile che soddisfi le richieste. Se ne trova uno disponibile lo associa a quel PVC altrimenti la richiesta rimane in stato di **pending** fino a che non si trova un PV che la soddisfi.

Viene appunto spiegato più nel dettaglio il ciclo di vita di queste richieste.

## 6. Monitoraggio e Loggging

Monitorare lo stato del cluster e delle applicazioni in esecuzione su Kubernetes è essenziale per garantire la stabilità, le prestazioni e la disponibilità del sistema. Il monitoraggio e il logging aiutano a rilevare problemi prima che possano diventare critici.

Per quanto riguarda il **MONITORAGGIO** uno dei primi aspetti da monitorare è lo stato del cluster stesso e per farlo si utilizzano strumenti di monitoraggio che raccolgono metriche sulle diverse componenti del cluster. Uno dei più utilizzati è **Prometheus**. Per il quale è stata prodotta una guida all'installazione attraverso il gestore di pacchetti **HELM** visto che spesso sono installati assieme ho proposto anche come accedere a **Grafana**.

Oltre al monitoraggio anche il **LOGGING** è fondamentale per capire il comportamento delle applicazioni in esecuzione nel cluster e tracciare la causa dei problemi. Kubernetes genera grandi quantità di log e quindi centralizzare e gestire questi log risulta importante per capirci qualcosa. Una delle soluzioni più gettonate è quella dell'integrazione dell'**ELK STACK** dove sono presenti **ElasticSearch, LogStash e Kibana**. Anche per questo come nel caso precedente ho prodotto una guida all'installazione e all'avvio sempre utilizzando **HELM** come gestore dei pacchetti.

## 7. Sicurezza

La sicurezza è una questione importante nella gestione di un cluster K8S perchè i servizi esposti possono essere vittima di attacchi informatici.

Partendo dai Pod e dai Container una informazione interessante che ho trovato è quella di evitare di seguire guide o materiali che fanno uso delle **Pod Security Policies** che sono state prima deprecate e poi rimosse. Al loro posto sono state inserite le **Pod Security Admission** che possono applicare in modo semplice diversi livelli di sicurezza a livello di namespace.

Per garantire una maggiore sicurezza è bene seguire anche una serie di **best practice** come **utilizzare immagini sicure**, **utilizzare le PSA**, utilizzare **strumenti di monitoraggio della sicurezza** in fase di esecuzione e **SOPRATTUTTO LIMITARE LE RISORSE** assegnate ai pods, per esempio con `limits` o `request` nella fase di definizione del Pod per evitare attacchi di tipo DOS. Sotto ho poi posto un semplice esempio di come utilizzare `limits` e `requests`.

Altri aspetti importanti per la sicurezza sono il **CONTROLLO DEL TRAFFICO** e il **MONITORAGGIO DELLA RETE**. Un ruolo importante lo giocano le **Network Policies** attraverso le quali è possibile decidere quali pods possono inviare e ricevere traffico. Inoltre sarebbe importante l'utilizzo di strumenti di monitoraggio del traffico per avere una visione più chiara del traffico e segnalare anomalie.

## 8. Dashboard

Ho poi dedicato un breve capitolo alle Dashboard, in quanto sono presenti diverse possibilità tra cui è possibile scegliere e sono uno strumento importante per chi è meno avvezzo ad un uso di Kubernetes da terminale. Ho elencato oltre a quelle predefinite anche quelle che online ho visto essere le più gettonate.

Magari dopo quando vediamo l'esempio apro quella di Minikube.

## 9. BEST PRACTICE

Il capitolo 9 è dedicato alle best practice, sostanzialmente non aggiunge niente perchè sono tutte cose già dette, ma fa un elenco delle best practice per utilizzare K8S in modo corretto e fa capire che le cose dette in precedenza non sono cose teoriche inutili ma sono cose importanti. Le uniche cose nuove sono **ELIMINARE RISORSE ORFANE** e **BACKUP DEI DATI E DELLE CONFIGURAZIONI**. Il primo è importante per non sprecare risorse con risorse inutilizzate e il secondo è importante per garantire il ripristino in caso di incovenienti.

## 10. Conclusione

L'ultimo capitolo rappresenta una mia conclusione su K8S che dice che K8S fornisce sicuramente funzionalità importanti e presenta numerosi vantaggi nel suo utilizzo. Tuttavia è anche un argomento molto complesso e insidioso che può causare problemi. 

Non presenta particolari svantaggi, tuttavia il rischio è che non sia la scelta giusta a causa della elevata complessità e dei costi che ne derivano. Non solo in termini economici, infatti repliche, nodi e cluster che su Minikube in locale ovviamente non costituiscono problemi in una applicazione in produzione su un cloud provider rappresentano un costo non trascurabile. Costi importanti sono anche quelli riguardanti il tempo e la complessità operativa e i costi derivanti dalla formazione del personale che deve certamente essere avanzata.

Un altro possibile problema è costituito dal vendor lock-in in quanto l'utilizzo di funzionalità avanzate di un dato provider può vincolarci a quello e impedirci di cambiare in modo rapido e semplice in caso di volontà di cambiare.