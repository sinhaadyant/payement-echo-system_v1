# Language Testing Guide

## Testing All Supported Languages

The Payment Echo System supports 11 languages. Use the `Accept-Language` header to test each language.

## Supported Languages

1. **Hindi (hi)** - हिंदी (Default)
2. **English (en)** - English
3. **Spanish (es)** - Español
4. **French (fr)** - Français
5. **German (de)** - Deutsch
6. **Bengali (bn)** - বাংলা
7. **Tamil (ta)** - தமிழ்
8. **Telugu (te)** - తెలుగు
9. **Kannada (kn)** - ಕನ್ನಡ
10. **Russian (ru)** - Русский
11. **Chinese (zh)** - 中文

## Test Commands

### Test 404 Error Messages

```bash
# English
curl -H "Accept-Language: en" http://localhost:8080/api/v1/payments/00000000-0000-0000-0000-000000000000

# Spanish
curl -H "Accept-Language: es" http://localhost:8080/api/v1/payments/00000000-0000-0000-0000-000000000000

# French
curl -H "Accept-Language: fr" http://localhost:8080/api/v1/payments/00000000-0000-0000-0000-000000000000

# German
curl -H "Accept-Language: de" http://localhost:8080/api/v1/payments/00000000-0000-0000-0000-000000000000

# Hindi (Default)
curl -H "Accept-Language: hi" http://localhost:8080/api/v1/payments/00000000-0000-0000-0000-000000000000
# Or without header (defaults to Hindi)
curl http://localhost:8080/api/v1/payments/00000000-0000-0000-0000-000000000000

# Bengali
curl -H "Accept-Language: bn" http://localhost:8080/api/v1/payments/00000000-0000-0000-0000-000000000000

# Tamil
curl -H "Accept-Language: ta" http://localhost:8080/api/v1/payments/00000000-0000-0000-0000-000000000000

# Telugu
curl -H "Accept-Language: te" http://localhost:8080/api/v1/payments/00000000-0000-0000-0000-000000000000

# Kannada
curl -H "Accept-Language: kn" http://localhost:8080/api/v1/payments/00000000-0000-0000-0000-000000000000

# Russian
curl -H "Accept-Language: ru" http://localhost:8080/api/v1/payments/00000000-0000-0000-0000-000000000000

# Chinese
curl -H "Accept-Language: zh" http://localhost:8080/api/v1/payments/00000000-0000-0000-0000-000000000000
```

### Test Validation Errors

```bash
# Hindi validation error
curl -X POST -H "Accept-Language: hi" -H "Content-Type: application/json" \
  -d '{"amount": -10, "currency": "USD", "status": "RECEIVED"}' \
  http://localhost:8080/api/v1/payments

# Tamil validation error
curl -X POST -H "Accept-Language: ta" -H "Content-Type: application/json" \
  -d '{"amount": -10, "currency": "USD", "status": "RECEIVED"}' \
  http://localhost:8080/api/v1/payments

# Russian validation error
curl -X POST -H "Accept-Language: ru" -H "Content-Type: application/json" \
  -d '{"amount": -10, "currency": "USD", "status": "RECEIVED"}' \
  http://localhost:8080/api/v1/payments
```

## Expected Responses

### Hindi (Default)

```json
{
  "timestamp": "2025-12-10T18:41:06.192980Z",
  "status": 404,
  "error": "संसाधन नहीं मिला - अनुरोधित संसाधन मौजूद नहीं है",
  "message": "आईडी के साथ भुगतान नहीं मिला: 00000000-0000-0000-0000-000000000000. कृपया भुगतान आईडी सत्यापित करें और पुनः प्रयास करें।"
}
```

### Tamil

```json
{
  "timestamp": "2025-12-10T18:41:06.192980Z",
  "status": 404,
  "error": "வளம் கிடைக்கவில்லை - அனுசரிக்கப்பட்ட வளம் இல்லை",
  "message": "ஐடி 00000000-0000-0000-0000-000000000000 உடன் பணம் கிடைக்கவில்லை. தயவுசெய்து பணம் ஐடியை சரிபார்க்கவும் மீண்டும் முயற்சிக்கவும்."
}
```

### Telugu

```json
{
  "timestamp": "2025-12-10T18:41:06.192980Z",
  "status": 404,
  "error": "వనరు కనుగొనబడలేదు - అభ్యర్థించిన వనరు లేదు",
  "message": "ID 00000000-0000-0000-0000-000000000000 తో చెల్లింపు కనుగొనబడలేదు. దయచేసి చెల్లింపు ID ని ధృవీకరించండి మరియు మళ్లీ ప్రయత్నించండి."
}
```

### Kannada

```json
{
  "timestamp": "2025-12-10T18:41:06.192980Z",
  "status": 404,
  "error": "ಸಂಪನ್ಮೂಲ ಕಂಡುಬಂದಿಲ್ಲ - ವಿನಂತಿಸಿದ ಸಂಪನ್ಮೂಲ ಅಸ್ತಿತ್ವದಲ್ಲಿಲ್ಲ",
  "message": "ID 00000000-0000-0000-0000-000000000000 ಜೊತೆ ಪಾವತಿ ಕಂಡುಬಂದಿಲ್ಲ. ದಯವಿಟ್ಟು ಪಾವತಿ ID ಅನ್ನು ಪರಿಶೀಲಿಸಿ ಮತ್ತು ಮತ್ತೆ ಪ್ರಯತ್ನಿಸಿ."
}
```

### Russian

```json
{
  "timestamp": "2025-12-10T18:41:06.192980Z",
  "status": 404,
  "error": "Ресурс не найден - запрошенный ресурс не существует",
  "message": "Платеж с ID 00000000-0000-0000-0000-000000000000 не найден. Пожалуйста, проверьте ID платежа и попробуйте снова."
}
```

## Testing in Postman

1. Open Postman collection
2. All requests now have `Accept-Language: hi` header by default
3. To test other languages, modify the header value:
   - Change `Accept-Language` header to `ta` for Tamil
   - Change to `te` for Telugu
   - Change to `kn` for Kannada
   - Change to `ru` for Russian
   - etc.

## Testing in Swagger UI

1. Open `http://localhost:8080/swagger-ui.html`
2. Click "Authorize" button
3. Add header: `Accept-Language: hi` (or any other language code)
4. Execute any API endpoint
5. Check error messages in the selected language

## Verification Checklist

- [ ] All 11 languages return localized error messages
- [ ] Default language (Hindi) works without header
- [ ] Validation errors are localized
- [ ] 404 errors are localized
- [ ] 400 errors are localized
- [ ] 500 errors are localized
- [ ] Postman collection has Hindi as default
- [ ] All POST requests have URLs and sample data
- [ ] EKS deployment guide is complete

## Troubleshooting

### Language not changing?

1. **Restart the application** - Message files are loaded at startup
2. **Check message files exist** - Verify `messages_XX.properties` files are in `src/main/resources/`
3. **Check encoding** - Ensure files are UTF-8 encoded
4. **Check header** - Verify `Accept-Language` header is being sent correctly
5. **Check logs** - Look for locale resolution errors in application logs

### Fallback to English?

- If a language file is missing, the system falls back to English (`messages.properties`)
- Ensure all message keys exist in all language files
- Check for typos in locale codes (e.g., `hi` not `hin`)

