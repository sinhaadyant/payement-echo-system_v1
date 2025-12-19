# Postman Language Selection Guide

## Language Dropdown in Postman

The Postman collection now includes a **language dropdown variable** that allows you to easily change the language for all API requests.

### How to Use the Language Dropdown

1. **Open Postman Collection**

   - Import `postman/PaymentEchoSystem.postman_collection.json`
   - Right-click on the collection → **Edit**

2. **Access Language Variable**

   - Go to the **Variables** tab
   - Find the `language` variable
   - You'll see a dropdown with 11 language options

3. **Select Language**

   - Click on the `language` variable value
   - Select from the dropdown:
     - **hi** - Hindi (हिंदी) - **Default**
     - **en** - English
     - **es** - Spanish (Español)
     - **fr** - French (Français)
     - **de** - German (Deutsch)
     - **bn** - Bengali (বাংলা)
     - **ta** - Tamil (தமிழ்)
     - **te** - Telugu (తెలుగు)
     - **kn** - Kannada (ಕನ್ನಡ)
     - **ru** - Russian (Русский)
     - **zh** - Chinese (中文)

4. **All Requests Use Selected Language**
   - Once you change the `language` variable, **ALL requests** in the collection will automatically use that language
   - The `Accept-Language` header in every request uses `{{language}}` variable
   - No need to manually change headers for each request!

### Example Workflow

1. Set `language` = `ta` (Tamil) in collection variables
2. Run any API request
3. Error messages will be in Tamil (தமிழ்)
4. Change `language` = `ru` (Russian)
5. Run the same request
6. Error messages will be in Russian (Русский)

## Supported Languages

| Code | Language | Native Name     |
| ---- | -------- | --------------- |
| hi   | Hindi    | हिंदी (Default) |
| en   | English  | English         |
| es   | Spanish  | Español         |
| fr   | French   | Français        |
| de   | German   | Deutsch         |
| bn   | Bengali  | বাংলা           |
| ta   | Tamil    | தமிழ்           |
| te   | Telugu   | తెలుగు          |
| kn   | Kannada  | ಕನ್ನಡ           |
| ru   | Russian  | Русский         |
| zh   | Chinese  | 中文            |

## Testing Different Languages

### Quick Test

1. Set `language` = `hi` → Run any request → Check error messages in Hindi
2. Set `language` = `ta` → Run any request → Check error messages in Tamil
3. Set `language` = `ru` → Run any request → Check error messages in Russian

### Using Collection Runner

1. Click collection → **Run**
2. Set `language` variable before running
3. All requests in the run will use the selected language

## GraphQL Requests

All GraphQL requests also support the `{{language}}` variable:

- **GraphQL - Query All Payments**
- **GraphQL - Query Payment by ID**
- **GraphQL - Query Payments by Status**
- **GraphQL - Query Payments by Currency**
- **GraphQL - Mutation Create Payment**
- **GraphQL - Mutation Create Payment (With Creditor/Debtor)**
- **GraphQL - Mutation Echo Payment**

GraphQL error messages will be in the selected language.

## Troubleshooting

### Language Not Changing?

1. **Check Variable Value**: Ensure `language` variable is set correctly
2. **Restart Application**: Language changes require application restart
3. **Check Header**: Verify `Accept-Language: {{language}}` header is present
4. **Clear Cache**: Restart Postman if variable changes don't reflect

### Default Language

- Default is **Hindi (hi)**
- If no `Accept-Language` header is sent, responses will be in Hindi
- To change default, modify the `language` variable value

## Benefits

✅ **Single Variable Control**: Change language for all requests at once  
✅ **Easy Testing**: Quickly test all languages  
✅ **Consistent**: All REST and GraphQL APIs use the same language  
✅ **User-Friendly**: Dropdown makes language selection easy

