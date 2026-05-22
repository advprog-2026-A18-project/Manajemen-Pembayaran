# Payment Service Monitoring

Monitoring pada Manajemen Pembayaran mengikuti pendekatan Module 11 Deployment on Kubernetes, yaitu memonitor resource usage aplikasi yang berjalan di Kubernetes menggunakan Metrics Server dan perintah `kubectl top`.

## Desain Monitoring

Monitoring dilakukan di level Kubernetes karena service pembayaran dijalankan sebagai container di dalam Pod. Pendekatan ini memantau resource aktual yang digunakan oleh Pod, seperti CPU dan memory, sehingga sesuai untuk mengevaluasi apakah deployment membutuhkan penyesuaian resource request, resource limit, atau jumlah replica.

Komponen yang digunakan:

- Kubernetes Deployment untuk menjalankan Pod `pembayaran-service`
- Kubernetes Service untuk mengekspos Pod di dalam cluster
- Metrics Server untuk mengumpulkan metrik CPU dan memory
- `kubectl top node` dan `kubectl top pod` untuk membaca resource usage

## Manifest

Manifest Kubernetes disimpan pada:

- `k8s/payment-deployment.yaml`
- `k8s/payment-service.yaml`

Deployment mendefinisikan resource request dan limit:

```yaml
resources:
  requests:
    cpu: 100m
    memory: 256Mi
  limits:
    cpu: 500m
    memory: 512Mi
```

Resource request digunakan agar Kubernetes dapat menjadwalkan Pod pada node yang memiliki resource cukup. Resource limit digunakan untuk mencegah service memakai CPU/memory melebihi batas yang ditentukan.

## Cara Menggunakan

Aktifkan Metrics Server pada Minikube:

```shell
minikube addons enable metrics-server
```

Verifikasi Metrics Server:

```shell
kubectl get pods,services -n kube-system
```

Terapkan manifest service pembayaran:

```shell
kubectl apply -f k8s/payment-deployment.yaml
kubectl apply -f k8s/payment-service.yaml
```

Verifikasi Deployment, Pod, dan Service:

```shell
kubectl get deployments
kubectl get pods
kubectl get services
```

Monitor resource node:

```shell
kubectl top nodes
```

Monitor resource Pod:

```shell
kubectl top pods
```

Monitor Pod pembayaran secara spesifik:

```shell
kubectl top pod -l app=pembayaran-service
```

## Contoh Penggunaan

Setelah Pod berjalan, jalankan:

```shell
kubectl top pod -l app=pembayaran-service
```

Contoh output:

```text
NAME                                  CPU(cores)   MEMORY(bytes)
pembayaran-service-xxxxxxxxx-yyyyy    45m          220Mi
```

Output tersebut digunakan untuk mengevaluasi apakah konfigurasi resource sudah cukup. Jika memory mendekati `512Mi`, maka limit perlu dinaikkan atau perlu dilakukan optimasi penggunaan memory. Jika CPU stabil mendekati `500m`, maka service dapat dipertimbangkan untuk scale out dengan menambah replica.

## Justifikasi

Pendekatan ini dipilih karena sesuai dengan Module 11 yang menekankan monitoring resource usage pada Kubernetes menggunakan Metrics Server. Monitoring dilakukan pada level Pod sehingga metrik yang diperoleh merepresentasikan konsumsi resource aktual service ketika berjalan sebagai container. Hal ini lebih relevan untuk deployment berbasis Kubernetes dibanding hanya mengekspos metrik aplikasi melalui endpoint internal.
