package com.shakti.ai.ai

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.withContext

/**
 * RunAnywhereAIService - Unified AI service using RunAnywhere SDK
 *
 * This service provides on-device AI inference for all ShaktiAI modules:
 * - Sathi AI (Emotional Support)
 * - Guardian AI (Safety Monitoring)
 * - Nyaya AI (Legal Assistance)
 * - DhanShakti AI (Financial Literacy)
 * - Gyaan AI (Educational Content)
 * - Swasthya AI (Health Monitoring)
 * - Raksha AI (Pattern Recognition)
 * - Sangam AI (Community Connections)
 *
 * Uses RunAnywhere SDK for privacy-first, on-device AI processing
 */
class RunAnywhereAIService private constructor(private val context: Context) {

    // System prompts for different AI modules
    private val systemPrompts = mapOf(
        "sathi" to """
            You are Sathi AI, a compassionate mental health companion for women in India.
            Your role:
            - Listen without judgment and provide emotional support
            - Provide culturally sensitive mental health guidance
            - Offer coping strategies and wellness techniques
            - Encourage professional help when needed
            - Support in Hindi, English, and regional languages
            - Focus on challenges faced by Indian women
            
            IMPORTANT RULES:
            - Never provide medical diagnosis
            - Always encourage seeing a professional for serious issues
            - Be supportive, empathetic, and respectful
            - Provide actionable advice
            - Keep responses concise and clear
            - Respect privacy and confidentiality
        """.trimIndent(),

        "guardian" to """
            You are Guardian AI, a safety monitoring system for women.
            Your role:
            - Analyze distress signals and threats
            - Provide immediate safety guidance
            - Help with emergency response planning
            - Offer safety tips and precautions
            
            IMPORTANT:
            - Prioritize safety above all
            - Provide clear, actionable safety instructions
            - Never minimize threats or concerns
            - Quick and direct responses in emergencies
        """.trimIndent(),

        "nyaya" to """
            You are Nyaya AI, a legal advisor for women's rights in India.
            Your expertise:
            - Indian Penal Code (IPC) sections related to women
            - Domestic Violence Act
            - Dowry Act
            - Sexual Harassment at Workplace (POSH) Act
            - Protection of Women from Sexual Harassment Act
            - Divorce and property laws
            
            Tasks:
            - Explain legal rights in simple terms
            - Provide relevant IPC section numbers
            - Suggest appropriate legal actions
            - Draft simple legal notices
            - Connect with legal resources
            
            IMPORTANT:
            - Provide section numbers with explanations
            - Always recommend professional legal counsel
            - Keep language simple and jargon-free
            - Be supportive and empowering
        """.trimIndent(),

        "dhanshakti" to """
            You are DhanShakti AI, a financial advisor for women's economic independence.
            Your expertise:
            - Micro-credit and loans
            - Investment strategies
            - Budgeting and savings
            - Business startup guidance
            - Government schemes for women (Mudra, Stand-Up India, etc.)
            - Financial literacy and planning
            
            Tasks:
            - Assess financial goals and create plans
            - Suggest government schemes and subsidies
            - Provide business ideas based on skills
            - Create budgets and savings plans
            - Explain financial concepts simply
            
            FOCUS:
            - Low-cost solutions for all income levels
            - Government subsidies and schemes
            - Risk-free investment options
            - Practical and achievable advice
        """.trimIndent(),

        "gyaan" to """
            You are Gyaan AI, an educational advisor for women's skill development.
            Your expertise:
            - Skill assessment and gap analysis
            - Career recommendations
            - Upskilling pathways
            - Scholarship and course recommendations
            - Industry demand analysis
            - Vocational training opportunities
            
            Tasks:
            - Identify skill gaps and learning needs
            - Suggest free/low-cost learning resources
            - Match with scholarships and grants
            - Create personalized learning timelines
            - Connect with vocational training programs
            
            FOCUS:
            - Women-centric education and skills
            - Free and low-cost resources
            - High-demand, practical skills
            - Flexible learning schedules
        """.trimIndent(),

        "swasthya" to """
            You are Swasthya AI, a reproductive health and wellness companion.
            Your expertise:
            - Menstrual cycle tracking and health
            - Reproductive health education
            - Symptom analysis and guidance
            - Nutrition for women
            - Sexual and reproductive rights
            - Wellness and preventive care
            
            Tasks:
            - Provide health education and guidance
            - Suggest when to consult specialists
            - Normalize menstruation discussions
            - Offer nutrition and wellness tips
            - Empower with health knowledge
            
            IMPORTANT:
            - Privacy is paramount
            - No diagnosis, only education and suggestions
            - Always recommend professional medical advice
            - Be sensitive and respectful
            - Use simple, clear language
        """.trimIndent(),

        "raksha" to """
            You are Raksha AI, a domestic violence support and pattern recognition system.
            Your expertise:
            - Domestic violence pattern recognition
            - Safety planning and escape routes
            - Emergency resources and contacts
            - Emotional support and crisis counseling
            - Legal remedies for domestic violence
            
            Tasks:
            - Identify abuse patterns early
            - Create personalized safety plans
            - Connect with shelters and NGOs
            - Provide psychological first aid
            - Guide through legal processes
            
            CRITICAL:
            - Maintain absolute confidentiality
            - Never minimize or dismiss abuse
            - Always prioritize safety
            - Emergency contacts readily available
            - Be compassionate and non-judgmental
        """.trimIndent(),

        "sangam" to """
            You are Sangam AI, a community connections and networking advisor.
            Your expertise:
            - Safe women's communities and groups
            - Support networks and mentorship
            - Local resources and services
            - Community events and activities
            - Peer support matching
            
            Tasks:
            - Recommend safe communities and groups
            - Connect with mentors and role models
            - Suggest relevant events and workshops
            - Build support networks
            - Foster community engagement
            
            FOCUS:
            - Verified and safe communities
            - Supportive and empowering environments
            - Local and accessible resources
            - Privacy and security
        """.trimIndent()
    )

    /**
     * Get available models from RunAnywhere SDK using reflection
     */
    suspend fun getAvailableModels(): List<ModelInfo> = withContext(Dispatchers.IO) {
        try {
            val runAnywhereClass = Class.forName("com.runanywhere.sdk.public.RunAnywhere")
            val listAvailableModelsMethod = runAnywhereClass.getMethod("listAvailableModels")
            val models =
                listAvailableModelsMethod.invoke(null) as? List<*> ?: return@withContext emptyList()
            models.mapNotNull { model ->
                try {
                    model?.let {
                        ModelInfo(
                            id = it.javaClass.getMethod("getId").invoke(it) as String,
                            name = it.javaClass.getMethod("getName").invoke(it) as String,
                            size = 0L, // Size will be determined during download
                            isDownloaded = it.javaClass.getMethod("isDownloaded")
                                .invoke(it) as Boolean
                        )
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to parse model info", e)
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get available models", e)
            emptyList()
        }
    }

    /**
     * Download a model with progress tracking using reflection
     */
    suspend fun downloadModel(modelId: String): Flow<Float> = withContext(Dispatchers.IO) {
        try {
            val runAnywhereClass = Class.forName("com.runanywhere.sdk.public.RunAnywhere")
            val downloadModelMethod =
                runAnywhereClass.getMethod("downloadModel", String::class.java)
            val flow = downloadModelMethod.invoke(null, modelId) as Flow<*>
            flow as Flow<Float>
        } catch (e: Exception) {
            Log.e(TAG, "Failed to download model", e)
            emptyFlow()
        }
    }

    /**
     * Load a model for inference using reflection
     */
    suspend fun loadModel(modelId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val runAnywhereClass = Class.forName("com.runanywhere.sdk.public.RunAnywhere")
            val loadModelMethod = runAnywhereClass.getMethod("loadModel", String::class.java)
            val success = loadModelMethod.invoke(null, modelId) as Boolean
            if (success) {
                Log.d(TAG, "Model loaded successfully: $modelId")
            } else {
                Log.e(TAG, "Failed to load model: $modelId")
            }
            success
        } catch (e: Exception) {
            Log.e(TAG, "Error loading model: $modelId", e)
            false
        }
    }

    /**
     * Get current loaded model info using reflection
     */
    fun getCurrentModel(): String? {
        try {
            val runAnywhereClass = Class.forName("com.runanywhere.sdk.public.RunAnywhere")
            val currentModelField = runAnywhereClass.getField("currentModel")
            val currentModel = currentModelField.get(null)
            if (currentModel != null) {
                return currentModel.javaClass.getMethod("getName").invoke(currentModel) as String
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get current model", e)
        }
        return null
    }

    /**
     * Generate text with context for specific AI module using reflection
     */
    suspend fun generate(
        module: String,
        prompt: String,
        maxTokens: Int = 200,
        temperature: Float = 0.7f
    ): String = withContext(Dispatchers.IO) {
        try {
            val systemPrompt = systemPrompts[module] ?: ""
            val fullPrompt = if (systemPrompt.isNotEmpty()) {
                "$systemPrompt\n\nUser: $prompt\n\nAssistant:"
            } else {
                prompt
            }

            val runAnywhereGenerationOptionsClass =
                Class.forName("com.runanywhere.sdk.models.RunAnywhereGenerationOptions")
            val runAnywhereGenerationOptionsConstructor =
                runAnywhereGenerationOptionsClass.getConstructor(Int::class.java, Float::class.java)
            val options =
                runAnywhereGenerationOptionsConstructor.newInstance(maxTokens, temperature)

            val runAnywhereClass = Class.forName("com.runanywhere.sdk.public.RunAnywhere")
            val generateMethod = runAnywhereClass.getMethod(
                "generate",
                String::class.java,
                runAnywhereGenerationOptionsClass
            )
            val response = generateMethod.invoke(null, fullPrompt, options) as String
            response.trim()
        } catch (e: Exception) {
            Log.e(TAG, "Generation failed for module: $module", e)
            "I apologize, but I'm having trouble processing your request. Please try again."
        }
    }

    /**
     * Generate text with streaming for real-time responses using reflection
     */
    fun generateStream(
        module: String,
        prompt: String,
        maxTokens: Int = 200,
        temperature: Float = 0.7f
    ): Flow<String> {
        val systemPrompt = systemPrompts[module] ?: ""
        val fullPrompt = if (systemPrompt.isNotEmpty()) {
            "$systemPrompt\n\nUser: $prompt\n\nAssistant:"
        } else {
            prompt
        }

        return try {
            val runAnywhereGenerationOptionsClass =
                Class.forName("com.runanywhere.sdk.models.RunAnywhereGenerationOptions")
            val runAnywhereGenerationOptionsConstructor =
                runAnywhereGenerationOptionsClass.getConstructor(Int::class.java, Float::class.java)
            val options =
                runAnywhereGenerationOptionsConstructor.newInstance(maxTokens, temperature)

            val runAnywhereClass = Class.forName("com.runanywhere.sdk.public.RunAnywhere")
            val generateStreamMethod = runAnywhereClass.getMethod(
                "generateStream",
                String::class.java,
                runAnywhereGenerationOptionsClass
            )
            val flow = generateStreamMethod.invoke(null, fullPrompt, options) as? Flow<*>
                ?: return emptyFlow()
            flow as Flow<String>
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate stream for module: $module", e)
            return emptyFlow()
        }
    }

    /**
     * Specialized methods for each AI module
     */

    // Sathi AI - Emotional Support
    suspend fun callSathiAI(userMessage: String): String {
        return generate("sathi", userMessage, maxTokens = 300, temperature = 0.8f)
    }

    fun callSathiAIStream(userMessage: String): Flow<String> {
        return generateStream("sathi", userMessage, maxTokens = 300, temperature = 0.8f)
    }

    // Guardian AI - Safety Monitoring
    suspend fun callGuardianAI(userMessage: String): String {
        return generate("guardian", userMessage, maxTokens = 150, temperature = 0.5f)
    }

    // Nyaya AI - Legal Assistance
    suspend fun callNyayaAI(userMessage: String): String {
        return generate("nyaya", userMessage, maxTokens = 400, temperature = 0.7f)
    }

    fun callNyayaAIStream(userMessage: String): Flow<String> {
        return generateStream("nyaya", userMessage, maxTokens = 400, temperature = 0.7f)
    }

    // DhanShakti AI - Financial Literacy
    suspend fun callDhanShaktiAI(userMessage: String): String {
        return generate("dhanshakti", userMessage, maxTokens = 350, temperature = 0.7f)
    }

    fun callDhanShaktiAIStream(userMessage: String): Flow<String> {
        return generateStream("dhanshakti", userMessage, maxTokens = 350, temperature = 0.7f)
    }

    // Gyaan AI - Educational Content
    suspend fun callGyaanAI(userMessage: String): String {
        return generate("gyaan", userMessage, maxTokens = 300, temperature = 0.7f)
    }

    fun callGyaanAIStream(userMessage: String): Flow<String> {
        return generateStream("gyaan", userMessage, maxTokens = 300, temperature = 0.7f)
    }

    // Swasthya AI - Health Monitoring
    suspend fun callSwasthyaAI(userMessage: String): String {
        return generate("swasthya", userMessage, maxTokens = 300, temperature = 0.7f)
    }

    fun callSwasthyaAIStream(userMessage: String): Flow<String> {
        return generateStream("swasthya", userMessage, maxTokens = 300, temperature = 0.7f)
    }

    // Raksha AI - Pattern Recognition
    suspend fun callRakshaAI(userMessage: String): String {
        return generate("raksha", userMessage, maxTokens = 250, temperature = 0.6f)
    }

    fun callRakshaAIStream(userMessage: String): Flow<String> {
        return generateStream("raksha", userMessage, maxTokens = 250, temperature = 0.6f)
    }

    // Sangam AI - Community Connections
    suspend fun callSangamAI(userMessage: String): String {
        return generate("sangam", userMessage, maxTokens = 250, temperature = 0.7f)
    }

    fun callSangamAIStream(userMessage: String): Flow<String> {
        return generateStream("sangam", userMessage, maxTokens = 250, temperature = 0.7f)
    }

    /**
     * Unload current model using reflection
     */
    suspend fun unloadModel() {
        try {
            val runAnywhereClass = Class.forName("com.runanywhere.sdk.public.RunAnywhere")
            val unloadModelMethod = runAnywhereClass.getMethod("unloadModel")
            unloadModelMethod.invoke(null)
            Log.d(TAG, "Model unloaded")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to unload model", e)
        }
    }

    // Model info data class
    data class ModelInfo(
        val id: String,
        val name: String,
        val size: Long,
        val isDownloaded: Boolean
    )

    companion object {
        private const val TAG = "RunAnywhereAIService"

        @Volatile
        private var instance: RunAnywhereAIService? = null

        fun getInstance(context: Context): RunAnywhereAIService {
            return instance ?: synchronized(this) {
                instance ?: RunAnywhereAIService(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }
}
