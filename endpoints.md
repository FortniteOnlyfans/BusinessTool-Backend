# 📘 API Dokumentation

Alle Responses (außer `/login`) folgen dem Standardformat:

```
{
  "status": "success" | "fail" | "expired",
  "reason": "string",
  "payload": {}
}
```

* `reason` und `payload` sind optional
* `payload` enthält je nach Endpoint die jeweilige Struktur

---

# 💰 Definitionen

## Geld

**Geld** = Objektstruktur:

```
{
  "name": string,
  "wert": number (EUR),
  *"zinsen": number (%),
  *"laufzeit": number (months)
}
```

> `*` = optionales Feld

---

# 🔐 Auth

## POST `/login`

### Request

```
{
  "username": string,
  "password": string
}
```

### Response

```
Authorization: token
```

---

## POST `/register`

### Request

```
{
  "username": string,
  "password": string
}
```

### Response

Standard Response Format

---

## GET `/deleteAcc`

### Header

```
Authorization: token
```

### Response

Standard Response Format

---

# 📁 Projekte

## GET `/project/<n>/info`

### Header

```
Authorization: token
```

### Response Payload

```
{
  "name": string,
  "type": string,
  *"latest": number,
  "versions": [number],
  "created": number (milliseconds)
}
```

---

## POST `/project/create`

### Header

```
Authorization: token
```

### Request

```
{
  "name": string,
  "type": string,
  "startKosten": [Geld]
}
```

### Response

```
{
  "projId": number
}
```

---

## POST `/project/<n>/delete`

### Header

```
Authorization: token
```

### Response

Standard Response Format

---

# 📊 Projekt Versionen

## GET `/project/<n>/version/<n>/info`

### Header

```
Authorization: token
```

### Response Payload

```
{
  "kosten": [Geld],
  "finanzierung": [Geld],
  "kapital": [Geld],
  "privat": [Geld],
  "ertrag": [Geld],
  "zeitspanne": number (months),
  "erstellt": number (milliseconds),
  "userName": string
}
```

---

## POST `/project/<n>/version/create`

### Header

```
Authorization: token
```

### Request

```
{
  *"kosten": [Geld],
  *"finanzierung": [Geld],
  *"kapital": [Geld],
  *"privat": [Geld],
  *"ertrag": [Geld],
  "zeitspanne": number (months)
}
```

### Response

```
{
  "projVerId": number
}
```

---

# 🧠 Legende

* `*feld` = optional
* `Geld` = definierte Struktur (siehe oben)
