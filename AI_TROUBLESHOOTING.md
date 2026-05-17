# TROUBLESHOOTING AI GENERATION FAILED ERROR

## 🔍 DIAGNOSTIC STEPS TO FIX "AI GENERATION FAILED"

Since you're getting "AI generation failed" in the frontend, let's systematically troubleshoot this issue.

### 🚨 STEP 1: Verify Backend is Running
First, check if your Spring Boot application is actually running and accessible:

**In a new terminal tab, run:**
```bash
# Check if the backend is responding
curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8000/actuator/health 2>/dev/null || echo "Backend not responding"

# If actuator isn't enabled, try a basic endpoint
curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8000/api/auth/me 2>/dev/null || echo "Backend not responding"
```

**Expected output:** `200` or similar success code
**If you see:** `Connection refused` or `000` → Backend is not running

### 🚨 STEP 2: Check if Backend Started Successfully
If the backend isn't responding, check if it's starting correctly:

**In your project directory, run:**
```bash
./mvnw spring-boot:run
```

**Look for these key lines in the startup logs:**
- `Started SmartrecruteApplication in X seconds`
- No `[ERROR]` messages during startup
- If you see compilation errors, we need to fix those first

### 🚨 STEP 3: Test Ollama Connectivity
The AI features depend on Ollama running with the llama3.2 model:

**In another terminal tab, run:**
```bash
# Check if Ollama service is running
curl -s http://localhost:11434/api/version || echo "Ollama not running"

# Check if llama3.2 model is available
ollama list | grep llama3.2 || echo "Model not found - run: ollama pull llama3.2"

# Test Ollama directly
curl -s -X POST http://localhost:11434/api/generate \
  -H "Content-Type: application/json" \
  -d '{"model": "llama3.2", "prompt": "Hello", "stream": false}' | head -c 100
```

### 🚨 STEP 4: Test Backend AI Endpoints Directly
Test if the backend AI endpoints are working:

**Job Description Test:**
```bash
curl -s -X POST http://localhost:8000/api/ai/generate-description \
  -H "Content-Type: application/json" \
  -d '{"titre": "Software Engineer"}' | head -c 100
```

**Interview Questions Test:**
```bash
curl -s -X POST http://localhost:8000/api/ai/interview-questions \
  -H "Content-Type: application/json" \
  -d '{"skill": "Java", "jobTitle": "Developer"}' | head -c 100
```

### 🚨 STEP 5: Check Frontend Configuration
Verify your frontend AI service is pointing to the correct URL:

**Check:** `frontend/src/app/core/ai.service.ts`
- Line 6: `private baseUrl = 'http://localhost:8000/api/ai';`
- This should match your backend URL

### 🚨 STEP 6: Check Browser Console for Detailed Errors
When you get "AI generation failed" in the frontend:
1. Open browser developer tools (F12)
2. Go to the Network tab
3. Look for failed requests to `/api/ai/*` endpoints
4. Click on the failed request to see:
   - Status code (404, 500, etc.)
   - Response body (actual error message)
   - Request payload being sent

## 📋 COMMON SOLUTIONS BASED ON ERROR TYPES:

### ❌ "Connection refused" or timeout:
- **Solution:** Start the backend: `./mvnw spring-boot:run`
- **Solution:** Start Ollama: `ollama serve`
- **Solution:** Pull model: `ollama pull llama3.2`

### ❌ 404 Not Found:
- **Solution:** Backend not running on expected port
- **Solution:** Check if backend started on different port (check application.properties)
- **Solution:** Verify endpoint spelling matches exactly

### ❌ 500 Internal Server Error:
- **Solution:** Check backend logs for stack trace
- **Solution:** Usually indicates an exception in the AI service
- **Solution:** Often related to Ollama not being available

### ❌ 400 Bad Request:
- **Solution:** Check what data you're sending in the request
- **Solution:** Verify JSON format matches what backend expects

## 🛠️ QUICK VERIFICATION SCRIPT:

Run this to check all components at once:

```bash
echo "=== Checking Backend ==="
curl -s http://localhost:8000/actuator/health 2>/dev/null || echo "❌ Backend not responding"

echo "=== Checking Ollama ==="
curl -s http://localhost:11434/api/version 2>/dev/null || echo "❌ Ollama not running"
ollama list | grep -q llama3.2 && echo "✅ llama3.2 model available" || echo "❌ llama3.2 model missing (run: ollama pull llama3.2)"

echo "=== Testing AI Endpoints ==="
curl -s -X POST http://localhost:8000/api/ai/generate-description -H "Content-Type: application/json" -d '{"titre":"Test"}' 2>/dev/null | head -c 50 && echo " (AI endpoint)" || echo "❌ AI endpoint failed"
```

## 💡 IF YOU SEE "AI GENERATION FAILED" IN FRONTEND:

This usually means:
1. The frontend made a request to `/api/ai/*` endpoint
2. The request failed (network error, timeout, or HTTP error status)
3. The frontend service caught the error and displayed "AI generation failed"

**To get the actual error:**
1. Open browser DevTools → Network tab
2. Reproduce the error
3. Find the failing `/api/ai/*` request
4. Click it → Look at "Response" tab for actual error message
5. Check "Headers" tab for status code

## ✅ VERIFICATION WHEN WORKING:

When everything is working correctly, you should see:
- Backend logs showing successful requests to Ollama
- HTTP 200 responses from AI endpoints
- JSON responses with AI-generated content (not mock responses)
- No errors in frontend console

## 🚨 IMPORTANT NOTE ABOUT PREVIOUS ERRORS:

The "Provider returned error" messages you've seen in our chat history **ARE NOT FROM YOUR APPLICATION**. Those were external errors from the AI assistant helping us troubleshoot.

Your current "AI generation failed" error **IS FROM YOUR APPLICATION** and indicates a real issue that needs fixing using the steps above.

**Start with Step 1: Verify your backend is actually running and accessible.**