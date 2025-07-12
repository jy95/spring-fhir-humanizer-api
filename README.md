# spring-fhir-humanizer-api [![Codacy Badge](https://app.codacy.com/project/badge/Grade/8ceab67e761147f398a3df72d1e0b741)](https://app.codacy.com/gh/jy95/spring-fhir-humanizer-api/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade) [![Codacy Badge](https://app.codacy.com/project/badge/Coverage/8ceab67e761147f398a3df72d1e0b741)](https://app.codacy.com/gh/jy95/spring-fhir-humanizer-api/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_coverage)

**spring-fhir-humanizer-api** is a Spring Boot REST API powered by [FDS](https://github.com/jy95/fds) that converts 
* FHIR R4 [`Dosage`](https://hl7.org/fhir/R4/dosage.html) and [`Timing`](https://hl7.org/fhir/R4/datatypes.html#timing) elements 
* FHIR R5 [`Dosage`](https://hl7.org/fhir/R5/dosage.html) and [`Timing`](https://hl7.org/fhir/R5/datatypes.html#timing) elements

into **human-readable text** — in your desired language and with customizable formatting & more.

## 🚀 Demo

- Swagger UI : https://spring-fhir-humanizer-api.onrender.com/swagger-ui/index.html 
- OpenAPI : https://spring-fhir-humanizer-api.onrender.com/v3/api-docs 
- API : https://spring-fhir-humanizer-api.onrender.com

> ⚠️ **Note:** This instance is hosted on [Render](https://render.com), which puts inactive web services to sleep. The first request after a period of inactivity may take **up to 50 seconds** to respond.

## ✨ Features

- 📝 Converts FHIR **Dosage** and **Timing** elements to plain-language descriptions
- 🌐 Supports **internationalization** (via locale parameter)
- ⚙️ Customize display format via options like `displayOrder` and `displaySeparator`
- 📦 Accepts single or multiple objects
- 🔍 Includes built-in **Swagger UI** for easy testing and exploration
- ✂️ Choose output format style :
  - `SUMMARY` : all results concatenated into a single string
  - `DETAILED` : generate one string per entry 

---

## 🚀 Getting Started

### Prerequisites

- Java 21+
- Maven

### Build and Run

```bash
git clone https://github.com/your-org/spring-fhir-humanizer-api.git
cd spring-fhir-humanizer-api
./mvnw spring-boot:run
```

## Credits

Special thanks to : 
- [The Java library which this webservice uses](https://github.com/jy95/fds)
- Render.com for the free plan hosting

## Contributors

<a href="https://github.com/jy95/spring-fhir-humanizer-api/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=jy95/spring-fhir-humanizer-api" alt="Contributors" />
</a>

## Star History

[![Star History Chart](https://api.star-history.com/svg?repos=jy95/spring-fhir-humanizer-api&type=Date)](https://star-history.com/#jy95/spring-fhir-humanizer-api&Date)