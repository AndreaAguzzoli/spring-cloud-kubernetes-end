# CODICE

## Esempio Originale

Il codice è preso da un esempio fatto in aula. Nell'esempio fatto in aula erano presenti 4 servizi:

1. `Composite-Service`
2. `Date-Service`
3. `Time-Service`
4. `Eureka-Server`

### Funzionamento

- 2 servizi che espongono **data** e **ora** (`date-service`, `time-service`)
- Un servizio che otteneva la data e l'ora dai 2 servizi precedenti ed esponeva una **combinazione** delle due (`composite-service`)
- Un **Discovery Server** (`Eureka-Server`)

## Versione K8S

La nuova versione gira su un **cluster Kubernetes**. In Kubernetes il Service Discovery avviene attraverso il **DNS interno** al cluster, quindi non si rende più necessario l'utilizzo di un servizio apposito per il Service Discovery.

Ogni **Service** viene **registrato automaticamente** nel DNS interno al cluster. I nomi dei Service internamente al DNS hanno questa forma: 
`<service-name>.<namespace>.svc.cluster.local`

Nell'esempio si utilizza Minikube e il **namespace di default**, ma il formato dei nomi è esattamente lo stesso di un cluster Kubernetes standard. I nomi saranno quindi:

- `date-service.default.svc.cluster.local`
- `time-service.default.svc.cluster.local`
- `composite-service.default.svc.cluster.local`

### Service Discovery

Ho deciso di utilizzare il Service Discovery di Kubernetes attraverso le **variabili d'ambiente**.

Sono state definite all'interno di `composite-service.yaml` nella parte di **Deployment**:

```composite-service.yaml
env:
    - name: SERVICE_DATE_SERVICE_URL
      value: "http://date-service:8080/date"
    - name: SERVICE_TIME_SERVICE_URL
      value: "http://time-service:8080/time"
```

Le variabili d'ambiente vengono poi passate a **Spring** nel file `application.yaml` di `composite-service`.

```application.yaml
service:
  date-service:
    url: ${SERVICE_DATE_SERVICE_URL}
  time-service:
    url: ${SERVICE_TIME_SERVICE_URL}
```

Sono poi utilizzate da `CompositeController`:

```CompositeController.java
@Value("${service.date-service.url}")
String urlDate;

@Value("${service.time-service.url}")
String urlTime;
```

La notazione utilizzata ('dot notation') serve per fare riferimento alla proprietà `url` nella configurazione gerarchica del file `application.yaml`. Quindi `@Value` inietta il valore corrispondente alle variabili d'ambiente `SERVICE_DATE_SERVICE_URL` e `SERVICE_TIME_SERVICE_URL`. 

Le variabili d'ambiente definite in Kubernetes verranno convertite da Spring nel formato con la **dot notation** all'interno di `application.yaml`.

Il controller è quindi in grado di chiamare i servizi con gli URL corretti. Per esempio quando fa la richiesta al servizio `date-service` la effettua all'URL:
`http://date-service:8080/date`

Qui entra in gioco Kubernetes che attraverso il suo **DNS interno** risolve il nome `date-service` con l'indirizzo IP assegnato al corrispondente Pod.

## Differenze

### Spring vs K8S

**Cosa continua a fare Spring:**

- Gestisce gli **URL** dei servizi attraverso `@Value` e `application.yaml`.
- Gestisce le **comunicazioni HTTP** tra i servizi attraverso `WebClient`.

**Cosa non fa più:**

- Non gestisce la scoperta dei servizi, si affida agli URL forniti da Kubernetes.

**Cosa fa Kubernetes:**

- Gestisce il **Service Discovery** risolvendo i nomi `date-service`, `time-service` nel cluster.
- Gestisce il **Networking** instradando le richieste HTTP dal Pod di `composite-service` a quelli di `date-service` e `time-service`.
- Garantisce che i Pod siano correttamente in esecuzione e ridireziona il traffico in caso di malfunzionamenti.

**Cosa non fa:**

- Non comprende la logica nè delle applicazioni nè dei dati che si scambiano. Si limita a fornire un supporto infrastrutturale per permettere ai servizi di comunicare in modo corretto ed efficiente.

# Parte teorica

## Introduzione

Kubernetes è un **orchestratore di container** che è diventata lo **standard de facto** per automatizzare il **deployment**, la **scalabilità** e la **gestione** di applicazioni containerizzate.

Uno dei principali vantaggi che porta è appunto di automatizzare molte operazioni come Deployment e scalabilità. Attraverso l'automatizzazione della **scalabilità** è in grado di mantenere uno **stato desiderato** delle applicazioni anche in caso di guasti. Attraverso l'automatizzazione della fase di **Deployment** vengono introdotte funzionalità importanti come **Rolling Updates** e **Rollback**.

## Architettura di K8S

Un Deployment Kubernetes è chiamato **Cluster** ed è costituito da un insieme di host che eseguono container Linux gestiti appunto da Kubernetes.

L'architettura di un cluster è basata su un modello **Master-Worker** dove il master è detto **Control Plane Node** mentre i nodi di elaborazione dove sono ospitati i **Pods** sono detti **Workers**.

### Control Plane Node

Tipicamente il **Control Plane Node** è responsabile delle decisioni globali sul cluster come **Scheduling** o **risposta agli eventi**. Tipicamente il Control Plane è presente su **un solo nodo** separato dagli workers, tuttavia sono possibili anche architetture **multi-master**.

### Workers

I **worker nodes** sono i nodi che **eseguono i container** delle applicazioni. Ogni worker contiene dei **pods** che contengono i containers (tipicamente uno). I **Workers** comunicano con il **Master** che **assegna loro i pods** e gestisce la loro **configurazione**. 

### Addons 

Sono funzionalità aggiuntive per ampliare le potenzialità del cluster. Portano funzionalità a volte essenziali come per esempio **DNS**, **Dashboard**, **monitoraggio**, **logging** e molto altro.

La maggior parte di essi possono essere integrati attraverso file `YAML`.

Ho poi dato la lista dei principali comandi per la loro gestione in Minikube.

### Pods

In Kubernetes non si parla mai di container, ma di **pods**. Essi sono **la più piccola unità di elaborazione** in Kubernetes e sono letteralmente dei contenitori che contengono uno o più pod. Tipicamente contengono un solo container, ma possono esserci casi in cui ne contengono diversi, in questo caso essi vengono gestiti come un unica entità che ha risorse condivise all'interno del pod.

I pod sono **entità usa e getta** e risiedono su un nodo del cluster dal momento in cui vengono creati fino a quando vengono terminati. Sono progettati per rappresentare una **singola istanza di una applicazione**, quindi per esempio per scalare orizzontalmente si creano più repliche dello stesso **pods**.

Essendo i pods elementi usa e getta ci sono problemi per quelle applicazioni che vogliono avere una **persistenza dei dati**. Per questo esistono **volumi** di diversi tipi che permettono di conservare i dati anche se i pods falliscono o vengono eliminati.

Per quanto riguarda il **networking** ad ogni pod viene assegnato un **indirizzo IP univoco nel cluster**. Per comunicare tra loro i pods utilizzano la rete del cluster attraverso gli indirizzi IP.

## Deploy e Gestione

### Risorse K8S

Per eseguire un progetto su Kubernetes si devono creare delle **risorse Kubernetes** sotto forma di file `YAML`. In questi file dobbiamo descrivere quale è lo stato desiderato della nostra applicazione e poi Kubernetes si occupa di mantenerlo in autonomia.

Molte di queste informazioni vengono descritte all'interno del **Deployment**. In esso vengono scritte le principali funzionalità come **numero di repliche**, **strategie di rollout e rollback** e molte altre. Sostanzialmente un Deployment **definisce come un'applicazione viene eseguita** all'interno del cluster ma non la rende disponibile alle altre.

Per esporre un pod è necessaria una risorsa di tipo **Service**. Essa rende un pod accessibile agli altri pod e agli utenti al di fuori del cluster, senza **Service** un pod non è accessibile. Kubernetes assegna ad ogni pod un IP, tuttavia esso gli appartiene finchè non termina, poi gliene viene assegnato un altro diverso. Questo risulta problematico per un client che vuole connettersi ad un servizio, ogni volta che un pod termina non sa più a chi connettersi. Qui entrano in gioco i **Service** che rappresenta una destinazione stabile, senza che i client debbano preoccuparsi degli indirizzi IP dinamici dei pods.

### Self Healing

È una delle funzionalità più potenti di Kubernetes. È progettata per garantire **disponibilità** e **resilienza** delle applicazioni distribuite. È la funzionalità che mantiene il nostro **stato desiderato**.

Si tratta della capacità di monitorare lo stato delle risorse e di intraprendere azioni correttive automatiche per mantenere lo stato desiderato:

- riavvio dei Pod in caso di failure
- sostituzione e ripianificazione dei pods in caso di failure
- mantenimento del numero di repliche
- terminare e riavviare i pod che non rispondono bene agli health-check

### Scaling

In Kubernetes è possibile lo scaling orizzontale aumentano il numero di repliche, ma anche lo scaling verticale aumentando le risorse destinate ai pods.

### Rollout & Rollback

**Rollout** è il processo attraverso il quale si apportano modifiche alle applicazioni. Kubernetes fornisce la possibilità di scegliere la **Strategy** con la quale propagare queste modifiche:

- **Recreate**
- **Rolling Updates**

Quando si parla di **Rollback** ci si riferisce alla **capacità di ritornare ad uno stato stabile precedente**. Questo è utile quando un aggiornamento introduce comportamenti imprevisti. 

Sono stati presentati anche una serie di comandi per la loro gestione in Kuberntes.

### Service Discovery & Load Balancing

Oltre al service discovery utilizzando le variabili d'ambiente è possibile anche gestire la scoperta dei servizi direttamente attraverso i **nomi-DNS** e i **namespaces**. 

Se `my-service` è il nome del servizio e `my-ns` è il nome del namespace, si dovrebbe riuscire a comunicare con il servizio attraverso il nome `my-service.my-ns`. Tuttavia non è stato verificato.

Quando si parla di **Load-Balancing** in Kubernetes ci si riferisce alla distribuzione del traffico tra i vari pods in modo da garantire un utilizzo uniforme ed efficiente delle risorse. È stata data una breve spiegazioni di quelli che sono i vari tipi di servizio possibili in Kubernetes per gestire il load balancing e una breve spiegazione di `Ingress` che è un **API-Object** che consente di applicare regole di routing avanzate che permettono una gestione del traffico avanzata e di applicare politiche di load-balancing particolari.

## Minikube 

È stato poi dedicato un intero capitolo a **Minikube** dove viene spiegato:
- di cosa di tratta
- le caratteristiche principali
- i vantaggi che porta
- una lista dei principali comandi che consentono di avviarlo, gestire un cluster, gestire le risorse, gestire il controllo degli accessi.

## Storage e Persistenza

È stato dedicato un capitolo alle varie **tipologie di volumi** che possono essere utilizzati e quali sono le loro caratteristiche.

## Monitoraggio e Logging

### Monitoraggio

In questo capitolo si parla di come gli strumenti di monitoraggio siano fondamentali per monitorare lo stato delle risorse del cluster e quindi per prevenire sovraccarichi.

Viene poi descritta una guida passo-passo per installare **Prometheus**, **Grafana** ed **Helm**, dove quest'ultimo è un utile gestore di pacchetti che facilita l'installazione non solo di Prometheus e di Grafana ma anche di molti altri utili strumenti.

### Logging

In questo capitolo si parla invece degli strumenti per il logging e di come essi siano importanti per comprendere meglio il comportamento delle applicazioni e risalire quindi alle cause dei problemi.

Viene introdotta una guida passo-passo su come installare (attraverso il gestore di pacchetti **Helm**) **ElastichSearch**, **Kibana** e **FileBeat**.

## Sicurezza

Le Sicurezza è un aspetto cruciale in Kubernetes, infatti le applicazioni e i servizi esposti possono essere bersaglio di attacchi informatici.

Una curiosità interessante è che ho trovato diversi tutorial o guide che parlano delle **PSP (Pod Security Policies)** che sono prima state deprecate e poi rimosse. 

Al loro posto sono state inserite le **PSA (Pod Security Admission)**. Esse garantiscono livelli diversi di sicurezza a livello di namespace e garantiscono quindi che i pod siano creati con quelle configurazioni di sicurezza.

Sono poi state citate alcune delle principali **Best Practices per la sicurezza**:
- Utilizzare **immagini sicure**
- Utilizzare le **PSA**
- Utilizzare sistemi di **Monitoraggio per rilevare comportamenti sospetti**
- **Limitare le risorse assegnate ai Pods** per evitare attacchi DoS.

## Dashboard

Una cosa molto utile e gradevole in Kubernetes sono le dashboard che possono essere utilizzate per osservare e gestire un cluster Kubernetes dalle persone che non hanno familiarità o a cui non piace usare una CLI.

In questo capitolo Sono elencate le principali dashboard che solitamente vengono utilizzate.

## Best Practices

Questo capitolo non introduce nulla di nuovo, semplicemente è un elenco delle best practices già citate precedentemente.

## Conclusione

Possiamo concludere dicendo che Kubernetes ha funzionalità che portano grandi vantaggi, ma porta con sè numerose insidie e costi, sia in termini economici che in termini di tempo:

- Economici ${\rightarrow}$ In questo caso è stato utilizzato attraverso Minikube in locale e quindi non abbiamo nessun costo, ma in caso reale ogni cluster, ogni nodo e ogni risorsa comporta costi aggiuntivi che vanno attentamente valutati e monitorati.
- Tempo ${\rightarrow}$ Portando con sè una grande complessità è difficile da imparare e il personale va correttamente formato e ci vuole diverso tempo per imparare ad usarlo correttamente.