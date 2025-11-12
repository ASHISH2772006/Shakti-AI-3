package com.shakti.ai.ai

import android.content.Context
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.shakti.ai.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * GeminiService - Unified AI service with RunAnywhere SDK integration
 *
 * Priority:
 * 1. RunAnywhere SDK (on-device, privacy-first) - Primary
 * 2. Gemini API (cloud-based) - Fallback when on-device model unavailable
 *
 * This service now acts as a bridge between RunAnywhereAIService and Gemini API
 */
class GeminiService(private val context: Context) {

    private val apiKey: String by lazy {
        try {
            // Try to get API key from BuildConfig
            val buildConfigClass = Class.forName("com.shakti.ai.BuildConfig")
            val apiKeyField = buildConfigClass.getField("GEMINI_API_KEY")
            val key = apiKeyField.get(null) as? String ?: "DEMO_MODE"
            if (key.isBlank()) "DEMO_MODE" else key
        } catch (e: Exception) {
            Log.w(TAG, "Failed to get API key from BuildConfig: ${e.message}")
            "DEMO_MODE"
        }
    }

    private val isApiKeyValid: Boolean by lazy {
        apiKey != "DEMO_MODE" && apiKey != "your_api_key_here" && apiKey.isNotBlank()
    }

    // Get RunAnywhere AI Service instance - with error handling
    private val runAnywhereService: RunAnywhereAIService? by lazy {
        try {
            RunAnywhereAIService.getInstance(context)
        } catch (e: Exception) {
            Log.w(TAG, "RunAnywhere SDK not available: ${e.message}")
            null
        }
    }

    // Check if RunAnywhere SDK is ready (model loaded)
    private fun isRunAnywhereReady(): Boolean {
        return try {
            runAnywhereService?.getCurrentModel() != null
        } catch (e: Exception) {
            false
        }
    }

    // System instructions for different AI purposes
    private val sathiSystemInstruction = """
        You are Sathi AI, a compassionate mental health companion for women.
        Your role:
        - Listen without judgment
        - Provide culturally sensitive mental health support
        - Offer coping strategies and techniques
        - Encourage professional help when needed
        - Support in Hindi, English, and regional languages
        - Focus on Indian women's specific challenges
        
        IMPORTANT RULES:
        - Never provide medical diagnosis
        - Always encourage seeing a professional for serious issues
        - Be supportive and empathetic
        - Provide actionable advice
        - Keep responses concise (under 500 chars)
    """.trimIndent()

    private val nyayaSystemInstruction = """
        You are Nyaya AI, a legal advisor for women's rights in India.
        Your expertise:
        - Indian Penal Code (IPC) sections related to women
        - Domestic Violence Act
        - Dowry Act
        - Sexual Harassment at Workplace (POSH) Act
        - Protection of Women from Sexual Harassment Act
        - Divorce and property laws
        
        Tasks:
        - Auto-generate FIRs based on victim complaints
        - Explain legal rights in simple terms
        - Draft legal notices and restraining orders
        - Suggest appropriate legal actions
        - Connect with pro-bono lawyers
        
        IMPORTANT:
        - Provide section numbers with explanations
        - Always recommend professional legal counsel
        - Keep language simple and jargon-free
    """.trimIndent()

    private val dhanShaktiSystemInstruction = """
        You are Dhan Shakti AI, a financial advisor for women's economic independence.
        Your expertise:
        - Micro-credit and loans
        - Investment strategies
        - Budgeting and savings
        - Business startup guidance
        - Government schemes for women
        - Financial literacy
        
        Tasks:
        - Assess loan eligibility
        - Create personalized investment plans
        - Suggest government schemes
        - Provide business ideas based on skills
        - Calculate financial goals timelines
        
        FOCUS:
        - Low-cost solutions for poor women
        - Government subsidies and schemes
        - Risk-free investment options
        - Savings discipline
    """.trimIndent()

    private val gyaanSystemInstruction = """
        You are Gyaan AI, an educational advisor for women's skill development.
        Your expertise:
        - Skill assessment
        - Career recommendations
        - Upskilling pathways
        - Scholarship finder
        - Course recommendations
        - Industry demand analysis
        
        Tasks:
        - Identify skill gaps
        - Suggest learning resources
        - Match with scholarships
        - Create learning timelines
        - Connect with vocational training
        
        FOCUS:
        - Women-centric education
        - Low-cost/free resources
        - High-demand skills
        - Flexible learning schedules
    """.trimIndent()

    private val swasthyaSystemInstruction = """
        You are Swasthya AI, a reproductive health companion.
        Your expertise:
        - Menstrual cycle tracking
        - Reproductive health education
        - Symptom analysis
        - Telemedicine facilitation
        - Nutrition for women
        - Sexual and reproductive rights
        
        Tasks:
        - Track menstrual cycles
        - Predict ovulation and fertile windows
        - Suggest health specialists
        - Provide health education
        - Connect with telemedicine doctors
        
        IMPORTANT:
        - Privacy is paramount
        - No diagnosis, only suggestions
        - Normalize menstruation discussions
        - Empower with knowledge
    """.trimIndent()

    private val rakshaSystemInstruction = """
        You are Raksha AI, a domestic violence support system.
        Your expertise:
        - Domestic violence patterns recognition
        - Safety planning
        - Emergency resources
        - Escape route planning
        - Emotional support
        - Legal remedies
        
        Tasks:
        - Identify abuse patterns
        - Create personalized safety plans
        - Connect with shelters and NGOs
        - Provide psychological first aid
        - Guide through legal processes
        
        CRITICAL:
        - Maintain absolute confidentiality
        - Never minimize abuse
        - Always prioritize safety
        - Emergency contacts readily available
    """.trimIndent()

    private val arogyaSystemInstruction = """
        You are Arogya AI, a health and wellness advisor.
        Your expertise:
        - General health advice
        - Nutrition planning
        - Fitness guidance
        - Disease prevention
        - Health education
        
        Tasks:
        - Provide general health advice
        - Create personalized nutrition plans
        - Suggest fitness routines
        - Educate on disease prevention
        - Connect with health specialists
        
        IMPORTANT:
        - Provide accurate and reliable information
        - Always recommend professional medical counsel
        - Keep language simple and jargon-free
    """.trimIndent()

    // Different specialized models for different AI purposes - LAZY INITIALIZATION (Gemini Fallback)
    private val sathiModel by lazy {
        GenerativeModel(
            modelName = "gemini-2.0-flash-exp",
            apiKey = apiKey
        )
    }

    private val nyayaModel by lazy {
        GenerativeModel(
            modelName = "gemini-2.0-flash-exp",
            apiKey = apiKey
        )
    }

    private val dhanShaktiModel by lazy {
        GenerativeModel(
            modelName = "gemini-2.0-flash-exp",
            apiKey = apiKey
        )
    }

    private val gyaanModel by lazy {
        GenerativeModel(
            modelName = "gemini-2.0-flash-exp",
            apiKey = apiKey
        )
    }

    private val swasthyaModel by lazy {
        GenerativeModel(
            modelName = "gemini-2.0-flash-exp",
            apiKey = apiKey
        )
    }

    private val rakshaModel by lazy {
        GenerativeModel(
            modelName = "gemini-2.0-flash-exp",
            apiKey = apiKey
        )
    }

    private val arogyaModel by lazy {
        GenerativeModel(
            modelName = "gemini-2.0-flash-exp",
            apiKey = apiKey
        )
    }

    // Generic model for other tasks
    private val generalModel by lazy {
        GenerativeModel(
            modelName = "gemini-2.0-flash-exp",
            apiKey = apiKey
        )
    }

    // Call Sathi AI for mental health - Uses RunAnywhere SDK first, Gemini as fallback
    suspend fun callSathiAI(userMessage: String): String = withContext(Dispatchers.IO) {
        try {
            // Try RunAnywhere SDK first (on-device)
            if (isRunAnywhereReady()) {
                Log.d(TAG, "Using RunAnywhere SDK for Sathi AI")
                return@withContext runAnywhereService?.callSathiAI(userMessage)
                    ?: getDemoResponse("sathi", userMessage)
            }

            // Fallback to Gemini API if available
            if (!isApiKeyValid) {
                return@withContext getDemoResponse("sathi", userMessage)
            }
            Log.d(TAG, "Using Gemini API for Sathi AI (fallback)")
            val fullPrompt = "$sathiSystemInstruction\n\nUser: $userMessage"
            val response = sathiModel.generateContent(fullPrompt)
            response.text ?: "I'm here to support you. Could you tell me more?"
        } catch (e: Exception) {
            Log.e(TAG, "Sathi AI error", e)
            "I encountered an issue. Please try again: ${e.message}"
        }
    }

    // Call Nyaya AI for legal advice
    suspend fun callNyayaAI(userMessage: String): String = withContext(Dispatchers.IO) {
        try {
            if (isRunAnywhereReady()) {
                Log.d(TAG, "Using RunAnywhere SDK for Nyaya AI")
                return@withContext runAnywhereService?.callNyayaAI(userMessage)
                    ?: getDemoResponse("nyaya", userMessage)
            }

            if (!isApiKeyValid) {
                return@withContext getDemoResponse("nyaya", userMessage)
            }
            Log.d(TAG, "Using Gemini API for Nyaya AI (fallback)")
            val fullPrompt = "$nyayaSystemInstruction\n\nUser: $userMessage"
            val response = nyayaModel.generateContent(fullPrompt)
            response.text ?: "Let me help you understand your legal rights."
        } catch (e: Exception) {
            Log.e(TAG, "Nyaya AI error", e)
            "Unable to process legal query: ${e.message}"
        }
    }

    // Call Dhan Shakti AI for financial advice
    suspend fun callDhanShaktiAI(userMessage: String): String = withContext(Dispatchers.IO) {
        try {
            if (isRunAnywhereReady()) {
                Log.d(TAG, "Using RunAnywhere SDK for DhanShakti AI")
                return@withContext runAnywhereService?.callDhanShaktiAI(userMessage)
                    ?: getDemoResponse("dhanshakti", userMessage)
            }

            if (!isApiKeyValid) {
                return@withContext getDemoResponse("dhanshakti", userMessage)
            }
            Log.d(TAG, "Using Gemini API for DhanShakti AI (fallback)")
            val fullPrompt = "$dhanShaktiSystemInstruction\n\nUser: $userMessage"
            val response = dhanShaktiModel.generateContent(fullPrompt)
            response.text ?: "Let's work on your financial independence."
        } catch (e: Exception) {
            Log.e(TAG, "DhanShakti AI error", e)
            "Financial calculation failed: ${e.message}"
        }
    }

    // Call Gyaan AI for education
    suspend fun callGyaanAI(userMessage: String): String = withContext(Dispatchers.IO) {
        try {
            if (isRunAnywhereReady()) {
                Log.d(TAG, "Using RunAnywhere SDK for Gyaan AI")
                return@withContext runAnywhereService?.callGyaanAI(userMessage)
                    ?: getDemoResponse("gyaan", userMessage)
            }

            if (!isApiKeyValid) {
                return@withContext getDemoResponse("gyaan", userMessage)
            }
            Log.d(TAG, "Using Gemini API for Gyaan AI (fallback)")
            val fullPrompt = "$gyaanSystemInstruction\n\nUser: $userMessage"
            val response = gyaanModel.generateContent(fullPrompt)
            response.text ?: "Let's find the best learning path for you."
        } catch (e: Exception) {
            Log.e(TAG, "Gyaan AI error", e)
            "Education suggestion failed: ${e.message}"
        }
    }

    // Call Swasthya AI for health
    suspend fun callSwasthyaAI(userMessage: String): String = withContext(Dispatchers.IO) {
        try {
            if (isRunAnywhereReady()) {
                Log.d(TAG, "Using RunAnywhere SDK for Swasthya AI")
                return@withContext runAnywhereService?.callSwasthyaAI(userMessage)
                    ?: getDemoResponse("swasthya", userMessage)
            }

            if (!isApiKeyValid) {
                return@withContext getDemoResponse("swasthya", userMessage)
            }
            Log.d(TAG, "Using Gemini API for Swasthya AI (fallback)")
            val fullPrompt = "$swasthyaSystemInstruction\n\nUser: $userMessage"
            val response = swasthyaModel.generateContent(fullPrompt)
            response.text ?: "Let me help with your health and wellness."
        } catch (e: Exception) {
            Log.e(TAG, "Swasthya AI error", e)
            "Health information unavailable: ${e.message}"
        }
    }

    // Call Raksha AI for domestic violence support
    suspend fun callRakshaAI(userMessage: String): String = withContext(Dispatchers.IO) {
        try {
            if (isRunAnywhereReady()) {
                Log.d(TAG, "Using RunAnywhere SDK for Raksha AI")
                return@withContext runAnywhereService?.callRakshaAI(userMessage)
                    ?: getDemoResponse("raksha", userMessage)
            }

            if (!isApiKeyValid) {
                return@withContext getDemoResponse("raksha", userMessage)
            }
            Log.d(TAG, "Using Gemini API for Raksha AI (fallback)")
            val fullPrompt = "$rakshaSystemInstruction\n\nUser: $userMessage"
            val response = rakshaModel.generateContent(fullPrompt)
            response.text ?: "Your safety is our priority. How can I help?"
        } catch (e: Exception) {
            Log.e(TAG, "Raksha AI error", e)
            "Emergency support unavailable: ${e.message}"
        }
    }

    // Call Arogya AI for general health advice
    suspend fun callArogyaAI(userMessage: String): String = withContext(Dispatchers.IO) {
        try {
            if (isRunAnywhereReady()) {
                Log.d(TAG, "Using RunAnywhere SDK for health advice")
                return@withContext runAnywhereService?.callSwasthyaAI(userMessage)
                    ?: getDemoResponse("arogya", userMessage)
            }

            if (!isApiKeyValid) {
                return@withContext getDemoResponse("arogya", userMessage)
            }
            Log.d(TAG, "Using Gemini API for Arogya AI (fallback)")
            val response = arogyaModel.generateContent(userMessage)
            response.text ?: "Let me provide you with general health advice."
        } catch (e: Exception) {
            Log.e(TAG, "Arogya AI error", e)
            "Health advice unavailable: ${e.message}"
        }
    }

    // Multi-turn conversation (chat history)
    suspend fun callSathiAIWithHistory(
        messages: List<Pair<String, String>>
    ): String = withContext(Dispatchers.IO) {
        try {
            // RunAnywhere SDK doesn't support chat history yet, use Gemini
            if (!isApiKeyValid) {
                return@withContext "Thank you for sharing. In demo mode, full conversation history is not available. Please add your Gemini API key in local.properties for full functionality."
            }
            val chat = sathiModel.startChat()
            for ((role, text) in messages) {
                chat.sendMessage(text)
            }
            val response = chat.sendMessage("Continue our conversation")
            response.text ?: "Let's continue our chat."
        } catch (e: Exception) {
            Log.e(TAG, "Chat history error", e)
            "Chat error: ${e.message}"
        }
    }

    // General purpose AI call
    suspend fun generateContent(prompt: String): String = withContext(Dispatchers.IO) {
        try {
            if (isRunAnywhereReady()) {
                Log.d(TAG, "Using RunAnywhere SDK for general content")
                return@withContext runAnywhereService?.generate("", prompt)
                    ?: "Demo mode: Please add your Gemini API key in local.properties for full AI functionality."
            }

            if (!isApiKeyValid) {
                return@withContext "Demo mode: Please add your Gemini API key in local.properties for full AI functionality."
            }
            Log.d(TAG, "Using Gemini API for general content (fallback)")
            val response = generalModel.generateContent(prompt)
            response.text ?: "No response generated"
        } catch (e: Exception) {
            Log.e(TAG, "Generate content error", e)
            "Error: ${e.message}"
        }
    }

    // Demo responses when API key is not configured and RunAnywhere not ready
    private fun getDemoResponse(module: String, userMessage: String): String {
        return when (module) {
            "sathi" -> "Thank you for sharing. I'm here to listen and support you. Please download and load an AI model from Settings for full on-device AI capabilities, or add your Gemini API key in local.properties for cloud-based responses."
            "nyaya" -> "I can help you understand your legal rights. Please download an AI model for full on-device legal advice, or add your Gemini API key for cloud assistance."
            "dhanshakti" -> "Let's work on your financial goals. Download an AI model for on-device financial advice, or add your Gemini API key for cloud assistance."
            "gyaan" -> "I can help you learn and grow. Download an AI model for on-device education guidance, or add your Gemini API key for cloud assistance."
            "swasthya" -> "Your health and wellness matter. Download an AI model for on-device health insights, or add your Gemini API key for cloud assistance."
            "raksha" -> "Your safety is our priority. Download an AI model for on-device safety planning, or add your Gemini API key for cloud assistance."
            "arogya" -> "Let's work on your health goals. Download an AI model for on-device health advice, or add your Gemini API key for cloud assistance."
            else -> "Demo mode active. Download an AI model or add Gemini API key for full functionality."
        }
    }

    companion object {
        private const val TAG = "GeminiService"

        @Volatile
        private var instance: GeminiService? = null

        fun getInstance(context: Context): GeminiService {
            return instance ?: synchronized(this) {
                instance ?: GeminiService(context.applicationContext).also { instance = it }
            }
        }
    }
}
