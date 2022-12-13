# Create a root certificate and private key to sign the certificates for your servicesCreate a root certificate and private key to sign the certificates for your services
mkdir example_certs1

openssl req -x509 -sha256 -nodes -days 365 -newkey rsa:2048 -subj '/O=example Inc./CN=example.com' -keyout example_certs1/example.com.key -out example_certs1/example.com.crt

#Generate a certificate and a private key for servicea.example.com:

openssl req -out example_certs1/servicea.example.com.csr -newkey rsa:2048 -nodes -keyout example_certs1/servicea.example.com.key -subj "/CN=servicea.example.com/O=servicea"

openssl x509 -req -sha256 -days 365 -CA example_certs1/example.com.crt -CAkey example_certs1/example.com.key -set_serial 0 -in example_certs1/servicea.example.com.csr -out example_certs1/servicea.example.com.crt

# Generate a client certificate and private key:

openssl req -out example_certs1/client.example.com.csr -newkey rsa:2048 -nodes -keyout example_certs1/client.example.com.key -subj "/CN=client.example.com/O=client organization"

openssl x509 -req -sha256 -days 365 -CA example_certs1/example.com.crt -CAkey example_certs1/example.com.key -set_serial 1 -in example_certs1/client.example.com.csr -out example_certs1/client.example.com.crt

# Create kubernetes secret

kubectl delete secret servicea-credential -n istio-ingress

kubectl create -n istio-ingress  secret tls servicea-credential --key=example_certs1/servicea.example.com.key --cert=example_certs1/servicea.example.com.crt

echo 'Generated all certificates'

#curl -v --resolve "servicea.example.com:31416:172.31.144.226" --cacert example_certs1/example.com.crt "https://servicea.example.com:31416/api/customer"
