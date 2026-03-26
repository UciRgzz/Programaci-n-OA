# Sistema Hospitalario - Microservicios REST

Proyecto de microservicios con Spring Boot que simula un sistema hospitalario real.

## Arquitectura

```
hospital-gateway (8080)  ←→  auth-service        (8081)
                         ←→  patient-service     (8082)
                         ←→  notification-service (8083)
```

## Requisitos

- Java 17+
- Maven 3.6+

## Instrucciones de ejecución

Abrir **4 terminales** y ejecutar cada servicio desde su carpeta:

```bash
# Terminal 1
cd auth-service
mvn spring-boot:run

# Terminal 2
cd patient-service
mvn spring-boot:run

# Terminal 3
cd notification-service
mvn spring-boot:run

# Terminal 4
cd hospital-gateway
mvn spring-boot:run
```

---

## Endpoints por servicio

### auth-service (puerto 8081)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/auth/login` | Login, retorna token |
| GET  | `/auth/validate?token=` | Valida un token |
| POST | `/auth/logout?token=` | Cierra sesión |

**Usuarios disponibles:** `admin/admin123`, `doctor/doc123`, `enfermera/enf123`

### patient-service (puerto 8082)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET    | `/patients` | Lista todos los pacientes |
| GET    | `/patients/{id}` | Obtiene paciente por ID |
| POST   | `/patients` | Registra nuevo paciente |
| PUT    | `/patients/{id}` | Actualiza paciente |
| DELETE | `/patients/{id}` | Elimina paciente |

### notification-service (puerto 8083)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET    | `/notifications` | Lista notificaciones |
| POST   | `/notifications/email` | Envía email |
| POST   | `/notifications/alerta` | Envía alerta del sistema |
| DELETE | `/notifications/{id}` | Elimina notificación |

---

## Endpoints del Gateway (puerto 8080)

### Endpoints simples (proxy)
```
POST /gateway/login
GET  /gateway/validate?token=
GET  /gateway/patients
GET  /gateway/patients/{id}
POST /gateway/patients
GET  /gateway/notifications
POST /gateway/notifications/email
POST /gateway/notifications/alerta
```

### Endpoints compuestos (composición de servicios)

#### 1. Login + Registro de paciente
`POST /gateway/login-y-registro`

```json
{
  "username": "admin",
  "password": "admin123",
  "nombre": "Carlos",
  "apellido": "Mendoza",
  "edad": 42,
  "diagnostico": "Hipertension",
  "telefono": "8111234567"
}
```

#### 2. Alta de paciente (valida token + registra + notifica)
`POST /gateway/alta-paciente?token=TU_TOKEN`

```json
{
  "nombre": "Laura",
  "apellido": "Torres",
  "edad": 30,
  "diagnostico": "Diabetes tipo 1",
  "telefono": "8119876543",
  "email": "doctor@hospital.com"
}
```

---

## Pruebas con cURL

### 1. Login
```bash
curl -X POST http://localhost:8080/gateway/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### 2. Operación compuesta: Login + Registro de paciente
```bash
curl -X POST http://localhost:8080/gateway/login-y-registro \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123","nombre":"Carlos","apellido":"Mendoza","edad":42,"diagnostico":"Hipertension","telefono":"8111234567"}'
```

### 3. Alta de paciente con token (reemplaza TOKEN con el token obtenido en el login)
```bash
curl -X POST "http://localhost:8080/gateway/alta-paciente?token=TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Laura","apellido":"Torres","edad":30,"diagnostico":"Diabetes tipo 1","telefono":"8119876543","email":"doctor@hospital.com"}'
```

### 4. Ver todos los pacientes
```bash
curl http://localhost:8080/gateway/patients
```

### 5. Ver notificaciones enviadas
```bash
curl http://localhost:8080/gateway/notifications
```
