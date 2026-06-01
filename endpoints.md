# 📘 API Dokumentation

Alle Responses (außer `/login`) folgen dem Standardformat:

```
{
  "status": "success" | "fail" | "expired",
  "reason": "string",
  *"payload": {}
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

## CalcResult

**CalcResult** = Objektstruktur:

```
{
  "kapitalbedarf": number,
  "umsatz": number,
  "kosten": number,
  "deckungsbeitrag": number,
  "gewinn": number
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
  "created": number (milliseconds),
  "startKosten": [Geld]
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

## GET `/project/version/<n>/info`

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
  "userName": string,
  "extra": {SpecificExtra}
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
  "zeitspanne": number (months),
  "extra": { SpecificExtra }
}
```

SpecificExtra:
Für Freemium:
```
{
    "basisNutzer": number,
    "premiumNutzer": number,
    "preisPremium": number,
    "aboZeit": number,
    "wachstumsrate": number,
    "varKosten": [Geld] (Geld pro Nutzer)
}
```


### Response

```
{
  "projVerId": number
}
```

# 🖩 Kalkulationen

## GET `/project/<n>/calc/latest`

### Header

```
Authorization: token
```

### Response
```
{CalcResult}
```

## GET `/project/calc/version/<n>`

### Header

```
Authorization: token
```

### Response
```
{CalcResult}
```

## GET `/project/<n>/calc/all`

### Header

```
Authorization: token
```

### Response
```
{
    "results": [
        {
            "date": number (milliseconds),
            "result": {CalcResult}
        }
    ]
}
```

---

# 🧠 Legende

* `*feld` = optional
* `Geld` = definierte Struktur (siehe oben)