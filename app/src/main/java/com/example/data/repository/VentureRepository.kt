package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.RevenueRecordEntity
import com.example.data.local.SprintTaskEntity
import com.example.data.model.MarketCandle
import com.example.data.model.MuskWisdom
import com.example.data.model.SignalDirection
import com.example.data.model.TradeSignal
import com.example.data.model.UnitEconomicItem
import com.example.data.model.VentureBlueprint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import kotlin.random.Random

class VentureRepository(private val database: AppDatabase) {

    // --- REVENUE TRACKING ---
    fun getAllRevenueRecords(): Flow<List<RevenueRecordEntity>> =
        database.revenueRecordDao().getAllRecords()

    fun getTodayRevenueRecords(): Flow<List<RevenueRecordEntity>> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return database.revenueRecordDao().getRecordsSince(calendar.timeInMillis)
    }

    fun getTodayTotalRevenue(): Flow<Double> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return database.revenueRecordDao().getTotalAmountSince(calendar.timeInMillis).map { it ?: 0.0 }
    }

    suspend fun logRevenue(title: String, amount: Double, category: String, notes: String = "") {
        database.revenueRecordDao().insertRecord(
            RevenueRecordEntity(
                title = title,
                amount = amount,
                timestamp = System.currentTimeMillis(),
                category = category,
                notes = notes
            )
        )
    }

    suspend fun deleteRevenueRecord(id: Long) {
        database.revenueRecordDao().deleteRecord(id)
    }

    // --- SPRINT TASKS ---
    fun getSprintTasks(): Flow<List<SprintTaskEntity>> =
        database.sprintTaskDao().getAllTasks()

    suspend fun toggleTaskCompletion(id: Long, completed: Boolean) {
        database.sprintTaskDao().setTaskCompleted(id, completed)
    }

    suspend fun seedInitialTasksIfEmpty() {
        val initial = listOf(
            SprintTaskEntity(
                title = "Deploy B2B Micro-Offer Landing Page",
                category = "LAUNCH",
                potentialYield = 45.0,
                isCompleted = false,
                orderIndex = 0
            ),
            SprintTaskEntity(
                title = "Execute 2 Scalping Signals (EUR/USD & Gold)",
                category = "TRADING",
                potentialYield = 35.0,
                isCompleted = true,
                orderIndex = 1
            ),
            SprintTaskEntity(
                title = "Send 15 Personalized AI Pitch DMs",
                category = "OUTREACH",
                potentialYield = 50.0,
                isCompleted = false,
                orderIndex = 2
            ),
            SprintTaskEntity(
                title = "Apply First Principles: Delete 2 redundant app features",
                category = "OPTIMIZE",
                potentialYield = 20.0,
                isCompleted = false,
                orderIndex = 3
            ),
            SprintTaskEntity(
                title = "Setup Stripe Instant Checkout with 1-click upsell",
                category = "PAYMENTS",
                potentialYield = 40.0,
                isCompleted = true,
                orderIndex = 4
            )
        )
        database.sprintTaskDao().insertTasks(initial)
    }

    // --- SEED INITIAL REVENUE IF EMPTY ---
    suspend fun seedInitialRevenueIfEmpty() {
        val records = listOf(
            RevenueRecordEntity(
                title = "AI Cold Outreach API - Monthly Plan",
                amount = 29.0,
                timestamp = System.currentTimeMillis() - 7200000,
                category = "SAAS",
                notes = "Stripe webhook processed"
            ),
            RevenueRecordEntity(
                title = "EUR/USD London Breakout TP Hit",
                amount = 38.50,
                timestamp = System.currentTimeMillis() - 14400000,
                category = "SIGNALS",
                notes = "+25 pips on 0.15 standard lot"
            ),
            RevenueRecordEntity(
                title = "AI Prompt Vault Digital Download",
                amount = 17.0,
                timestamp = System.currentTimeMillis() - 28800000,
                category = "PRODUCTS",
                notes = "Gumroad checkout"
            )
        )
        for (r in records) {
            database.revenueRecordDao().insertRecord(r)
        }
    }

    // --- SEEDED $100/DAY STARTUP BLUEPRINTS ---
    fun getStartupBlueprints(): List<VentureBlueprint> {
        return listOf(
            VentureBlueprint(
                id = "blueprint_ai_repurpose",
                title = "HyperClip AI Video Engine",
                tag = "HIGH MARGIN B2B",
                category = "AI Micro-SaaS",
                targetDailyYield = 100.0,
                targetMonthlyYield = 3000.0,
                formulaSummary = "20 clients paying \$150/mo = \$3,000/mo (\$100/day)",
                muskPrincipleAngle = "Automate human bottleneck. Podcasters spend 6 hours clipping reels. AI does it in 90 seconds for 1/10th the cost.",
                description = "Turn raw YouTube podcasts into 10 viral vertical TikTok/Reel clips with animated captions, viral hooks, and auto-scheduling.",
                unitEconomics = listOf(
                    UnitEconomicItem("Subscription Price", "\$150/month", "Billed on Stripe"),
                    UnitEconomicItem("Clients Needed", "20 active", "1 client sign-up every 1.5 days"),
                    UnitEconomicItem("API Cost / Client", "\$12/month", "Whisper + ffmpeg + Claude 3.5"),
                    UnitEconomicItem("Net Profit Margin", "92%", "Extreme cash efficiency")
                ),
                launchChecklist = listOf(
                    "Day 1: Build a simple Webhook pipeline (Make.com + Whisper API + Supabase).",
                    "Day 2: Scrape top 50 YouTube podcasts with 10k-100k subscribers.",
                    "Day 3: Send 3 pre-cut sample viral reels for FREE to each creator.",
                    "Day 4: Convert 4 of them to a \$150/mo pilot retainer."
                ),
                techStack = listOf("Next.js / Compose", "OpenAI Whisper", "FFmpeg Engine", "Stripe Billing"),
                acquisitionScript = "Hey [Creator Name], loved your interview with [Guest]. I clipped 3 high-retention TikTok reels with dynamic captions for you (attached). Free gift! If you want 30 of these every month on autopilot, I run an automated clipping engine for \$150/mo. Want me to handle next week's drop?"
            ),
            VentureBlueprint(
                id = "blueprint_algo_forex",
                title = "London-NY Liquidity Scalper",
                tag = "ALGORITHMIC CASHFLOW",
                category = "Algorithmic Trading",
                targetDailyYield = 120.0,
                targetMonthlyYield = 3600.0,
                formulaSummary = "25 pips/day with 0.5 lot sizing = \$125/day",
                muskPrincipleAngle = "Mathematical probability & strict risk limits. No emotional trading. Only enter when institutional order flow liquidity sweeps occur.",
                description = "High-precision scalping algorithm exploiting session overlap volatility in EUR/USD and Gold (XAU/USD) with predetermined 1:2.5 risk-to-reward stops.",
                unitEconomics = listOf(
                    UnitEconomicItem("Daily Target", "20-30 pips", "1 to 2 high-grade setups"),
                    UnitEconomicItem("Account Balance", "\$2,500 - \$5,000", "Using 1.5% max risk per trade"),
                    UnitEconomicItem("Position Size", "0.40 - 0.60 lot", "Strict Stop Loss always set"),
                    UnitEconomicItem("Win Rate Target", "68% - 75%", "Positive mathematical expectancy")
                ),
                launchChecklist = listOf(
                    "Day 1: Set up MetaTrader / TradingView alert webhooks.",
                    "Day 2: Backtest London open 08:00 GMT liquidity sweep on EUR/USD.",
                    "Day 3: Run paper trades using the Apex Telemetry calculator.",
                    "Day 4: Live deployment with 0.1 lot scaling up to full daily targets."
                ),
                techStack = listOf("TradingView Pine Script", "Telegram Signal Bot", "Apex Risk Calculator", "OANDA / IC Markets"),
                acquisitionScript = "Rule 1: Never risk more than \$30 to make \$75. Rule 2: Take profit 1 at +15 pips and move Stop Loss to Breakeven. Rule 3: Stop trading the moment \$100 daily goal is achieved."
            ),
            VentureBlueprint(
                id = "blueprint_headless_leads",
                title = "Niche LeadScraper B2B API",
                tag = "PASSIVE RECURRING",
                category = "Digital Arbitrage",
                targetDailyYield = 110.0,
                targetMonthlyYield = 3300.0,
                formulaSummary = "114 subscribers @ \$29/month = \$3,306/mo (\$110/day)",
                muskPrincipleAngle = "First Principles: High-growth agencies are starved for qualified decision-maker emails. Solve the raw data supply chain.",
                description = "A lightweight headless API and Google Sheet extension delivering verified owner phone numbers and verified business emails for local service businesses (roofers, plumbers, dentists).",
                unitEconomics = listOf(
                    UnitEconomicItem("Price Point", "\$29 - \$49/mo", "Micro-SaaS low-churn tier"),
                    UnitEconomicItem("Subscribers Needed", "80 - 110 users", "Less than 0.001% of market"),
                    UnitEconomicItem("Server Overhead", "\$25/month", "Cloudflare Workers + VPS"),
                    UnitEconomicItem("Gross Margin", "96%", "Pure digital software leverage")
                ),
                launchChecklist = listOf(
                    "Day 1: Write Google Maps Places API scraper script with email enrichment.",
                    "Day 2: Package as a 1-click Google Sheet template or web dashboard.",
                    "Day 3: Post a free 500-lead sample list in Facebook agency groups & X/Twitter.",
                    "Day 4: Put the full database behind a \$29/mo Stripe paywall."
                ),
                techStack = listOf("Cloudflare Workers", "Google Sheets API", "Hunter.io / Clearbit", "LemonSqueezy"),
                acquisitionScript = "I scraped all 2,400 licensed solar contractors in Texas with verified owner direct dials. Download the first 100 free here: [link]. If you want continuous fresh weekly exports, unlock full API access for \$29/mo."
            ),
            VentureBlueprint(
                id = "blueprint_prompt_matrix",
                title = "Executive AI Workflow Vault",
                tag = "HIGH CONVERTING ASSET",
                category = "Digital Products",
                targetDailyYield = 98.0,
                targetMonthlyYield = 2940.0,
                formulaSummary = "2 sales/day @ \$49 each = \$98/day",
                muskPrincipleAngle = "Zero marginal cost of reproduction. Create the asset once, sell infinitely at 99.9% net margin.",
                description = "Production-grade prompt architectures, system prompts, and automated Zapier/Make templates for startup founders to automate sales, legal contracts, and investor memos.",
                unitEconomics = listOf(
                    UnitEconomicItem("Retail Price", "\$49 per license", "One-time purchase with upsells"),
                    UnitEconomicItem("Sales Required", "2 per day", "Requires only ~60 visitors/day at 3.3% CVR"),
                    UnitEconomicItem("Fulfillment Cost", "\$0.00", "Instant digital file download"),
                    UnitEconomicItem("Upsell Opportunity", "\$199 Custom Consult", "Boosts daily run-rate by \$200")
                ),
                launchChecklist = listOf(
                    "Day 1: Curate 25 battle-tested executive workflows that save founders 10+ hours.",
                    "Day 2: Build sleek landing page with video walkthrough of 1 prompt in action.",
                    "Day 3: Share step-by-step breakdown on LinkedIn / X (Twitter).",
                    "Day 4: First sale within 24 hours of launch thread."
                ),
                techStack = listOf("Notion + Gumroad", "Loom Demo Video", "X/Twitter Content Engine", "Carrd Landing Page"),
                acquisitionScript = "Most people use ChatGPT like a toy. Here is the exact system prompt I used to generate a 12-page investor pitch deck and financial model in 4 minutes: [Thread preview]. Get the entire 50-workflow vault here for \$49."
            ),
            VentureBlueprint(
                id = "blueprint_ai_support_agency",
                title = "Omnichannel AI Support Bot Agency",
                tag = "HIGH TICKET SETUP",
                category = "B2B Automation",
                targetDailyYield = 140.0,
                targetMonthlyYield = 4200.0,
                formulaSummary = "1 client setup/week @ \$700 + \$200/mo retainer = \$140/day",
                muskPrincipleAngle = "Extreme customer delight through zero latency. Replace 3-day support email wait times with instant 2-second resolution.",
                description = "Build custom voice & text AI agents for Shopify stores and medical clinics that handle booking, order tracking, and refund policies 24/7.",
                unitEconomics = listOf(
                    UnitEconomicItem("Setup Fee", "\$700 - \$1,200", "Charged upfront per client"),
                    UnitEconomicItem("Monthly Retainer", "\$200/mo", "Maintenance & prompt tuning"),
                    UnitEconomicItem("Client Capacity", "6 active clients", "Hit \$4,000/mo easily"),
                    UnitEconomicItem("Setup Time", "3 hours per client", "Using Voiceflow or Botpress")
                ),
                launchChecklist = listOf(
                    "Day 1: Clone a Voiceflow template for e-commerce return requests.",
                    "Day 2: Audit 10 mid-size Shopify stores that have slow support.",
                    "Day 3: Send screen recording demoing their bot answering custom questions.",
                    "Day 4: Offer 14-day free trial, then \$700 setup upon proven ticket reduction."
                ),
                techStack = listOf("Voiceflow / Botpress", "Shopify Webhooks", "OpenAI Assistants API", "Zendesk Integration"),
                acquisitionScript = "Hi [Founder], I noticed your store took 38 hours to reply to my test inquiry about shipping to Canada. I built an AI agent trained on your actual FAQ that answered the same question in 1.4 seconds. Watch the 60-second video demo here: [link]."
            )
        )
    }

    // --- LIVE SIMULATED MARKET TELEMETRY & SIGNALS ---
    fun getLiveMarketSignals(): Flow<List<TradeSignal>> = flow {
        // Generates realistic live ticks
        val baseSignals = mutableListOf(
            TradeSignal(
                id = "sig_eur_usd",
                symbol = "EUR/USD",
                assetName = "Euro / US Dollar",
                assetType = "Forex",
                currentPrice = 1.0846,
                priceChangePercent = 0.42,
                direction = SignalDirection.BUY,
                entryPrice = 1.0835,
                stopLoss = 1.0812,
                takeProfit1 = 1.0880,
                takeProfit2 = 1.0925,
                winProbability = 82,
                riskRewardRatio = "1:2.8",
                timeframe = "15M",
                rationale = "Liquidity grab under Asian low + strong bullish displacement through 1.0830 order block. London session momentum accelerating.",
                recentCandles = generateMockCandles(1.0830, 0.0004)
            ),
            TradeSignal(
                id = "sig_xau_usd",
                symbol = "XAU/USD",
                assetName = "Gold Spot / USD",
                assetType = "Commodity",
                currentPrice = 2748.20,
                priceChangePercent = 1.15,
                direction = SignalDirection.BUY,
                entryPrice = 2742.00,
                stopLoss = 2731.50,
                takeProfit1 = 2765.00,
                takeProfit2 = 2780.00,
                winProbability = 86,
                riskRewardRatio = "1:3.2",
                timeframe = "1H",
                rationale = "Macro inflation hedge demand + institutional continuation flag. Target daily upside expansion to \$2,765 (+230 pips).",
                recentCandles = generateMockCandles(2740.0, 1.8)
            ),
            TradeSignal(
                id = "sig_gbp_usd",
                symbol = "GBP/USD",
                assetName = "British Pound / USD",
                assetType = "Forex",
                currentPrice = 1.2982,
                priceChangePercent = -0.28,
                direction = SignalDirection.SELL,
                entryPrice = 1.2995,
                stopLoss = 1.3020,
                takeProfit1 = 1.2940,
                takeProfit2 = 1.2900,
                winProbability = 79,
                riskRewardRatio = "1:2.2",
                timeframe = "30M",
                rationale = "Bearish fair value gap rejection at 1.3000 key resistance handle. Lower highs formed on 5M timeframe.",
                recentCandles = generateMockCandles(1.2990, 0.0006)
            ),
            TradeSignal(
                id = "sig_btc_usdt",
                symbol = "BTC/USDT",
                assetName = "Bitcoin / Tether",
                assetType = "Crypto",
                currentPrice = 68450.0,
                priceChangePercent = 2.45,
                direction = SignalDirection.BUY,
                entryPrice = 67800.0,
                stopLoss = 66900.0,
                takeProfit1 = 69800.0,
                takeProfit2 = 71500.0,
                winProbability = 81,
                riskRewardRatio = "1:2.4",
                timeframe = "1H",
                rationale = "Breakout above weekly consolidation range with rising spot CVD volume. Open interest expansion indicates trend continuation.",
                recentCandles = generateMockCandles(67500.0, 150.0)
            )
        )

        emit(baseSignals)

        // Keep emitting micro-ticks every 4 seconds to provide a live active market feel
        while (true) {
            delay(4000)
            val updated = baseSignals.map { sig ->
                val delta = (Random.nextDouble() - 0.48) * (sig.currentPrice * 0.0008)
                val newPrice = (sig.currentPrice + delta)
                sig.copy(
                    currentPrice = Math.round(newPrice * 10000.0) / 10000.0,
                    recentCandles = sig.recentCandles.takeLast(9) + MarketCandle(
                        timestamp = System.currentTimeMillis(),
                        open = sig.currentPrice,
                        high = maxOf(sig.currentPrice, newPrice) + 0.0002,
                        low = minOf(sig.currentPrice, newPrice) - 0.0002,
                        close = newPrice,
                        volume = Random.nextDouble(500.0, 2500.0)
                    )
                )
            }
            emit(updated)
        }
    }

    private fun generateMockCandles(startPrice: Double, volatility: Double): List<MarketCandle> {
        val list = mutableListOf<MarketCandle>()
        var curr = startPrice
        val now = System.currentTimeMillis()
        for (i in 10 downTo 0) {
            val open = curr
            val change = (Random.nextDouble() - 0.48) * volatility
            val close = open + change
            val high = maxOf(open, close) + Random.nextDouble() * volatility * 0.5
            val low = minOf(open, close) - Random.nextDouble() * volatility * 0.5
            list.add(
                MarketCandle(
                    timestamp = now - (i * 900000L),
                    open = open,
                    high = high,
                    low = low,
                    close = close,
                    volume = Random.nextDouble(100.0, 1000.0)
                )
            )
            curr = close
        }
        return list
    }

    // --- MUSK FIRST PRINCIPLES SYSTEM ---
    fun getMuskFirstPrinciples(): List<MuskWisdom> {
        return listOf(
            MuskWisdom(
                id = "principle_1",
                step = 1,
                title = "Question Every Requirement",
                subtitle = "Make requirements less dumb",
                quote = "Every requirement must come with a name, not a department. No matter how smart someone is, everyone is wrong some of the time.",
                tacticalBreakdown = "Most people fail to make \$100/day because they think they need a custom mobile app, legal trademarks, 6 months of code, and an LLC. Delete all of that. What is the physics of a sale? Someone gives you \$50 because you solved their pain. That only takes a Stripe link and an email.",
                diagnosticPrompt = "What unnecessary feature or credential are you waiting for before asking for your first \$50?"
            ),
            MuskWisdom(
                id = "principle_2",
                step = 2,
                title = "Delete The Part Or Process",
                subtitle = "If you're not adding back 10%, you didn't delete enough",
                quote = "The best part is no part. The best process is no process. It weighs nothing, costs nothing, can't fail.",
                tacticalBreakdown = "Cut the fat out of your product offer. If you're building a software tool, delete 4 out of 5 tabs. Focus 100% of your energy on the single button that creates economic value for the user.",
                diagnosticPrompt = "What 80% of your current project can you delete today to launch in the next 24 hours?"
            ),
            MuskWisdom(
                id = "principle_3",
                step = 3,
                title = "Simplify & Optimize",
                subtitle = "Never optimize something that shouldn't exist",
                quote = "The most common error of a smart engineer is to optimize a thing that should not exist.",
                tacticalBreakdown = "Don't spend days automating lead generation if you haven't validated that people actually want the offer manually. Sell 3 by hand first, then streamline the pipeline.",
                diagnosticPrompt = "Are you spending time decorating a website instead of talking to paying customers?"
            ),
            MuskWisdom(
                id = "principle_4",
                step = 4,
                title = "Accelerate Cycle Time",
                subtitle = "Iterate 10x faster than everyone else",
                quote = "The rate of innovation is what matters. If your feedback loop is 24 hours instead of 3 weeks, you will crush any competitor.",
                tacticalBreakdown = "Launch your \$100/day experiment by sunset. Test the offer on social media today. If nobody clicks, change the headline tomorrow morning.",
                diagnosticPrompt = "How can you compress this week's plan into the next 4 hours?"
            ),
            MuskWisdom(
                id = "principle_5",
                step = 5,
                title = "Automate & Scale",
                subtitle = "Only automate after steps 1 through 4",
                quote = "Automation comes last. If you automate a flawed process, you just produce garbage at hypersonic speed.",
                tacticalBreakdown = "Once your first 5 customers pay you \$100, plug in Webhooks, automated fulfillment, Stripe customer portals, and algorithmic trading scripts to run without your hands on the keyboard.",
                diagnosticPrompt = "What repetitive task has made money at least twice that you can now script or delegate to AI?"
            )
        )
    }

    // --- POSITION SIZING CALCULATOR LOGIC ---
    fun calculatePositionSize(
        accountBalance: Double,
        dailyTarget: Double = 100.0,
        riskPercent: Double = 1.5,
        targetPips: Double = 25.0
    ): Map<String, Any> {
        val maxDollarRisk = accountBalance * (riskPercent / 100.0)
        // 1 standard lot = $10 per pip on EUR/USD
        // pipValueNeeded = dailyTarget / targetPips
        val pipValueNeeded = dailyTarget / targetPips.coerceAtLeast(1.0)
        val suggestedLotSize = pipValueNeeded / 10.0 // approx standard lots
        val stopLossPips = maxDollarRisk / (suggestedLotSize * 10.0).coerceAtLeast(0.01)
        val riskReward = targetPips / stopLossPips.coerceAtLeast(1.0)

        return mapOf(
            "accountBalance" to accountBalance,
            "dailyTarget" to dailyTarget,
            "maxDollarRisk" to maxDollarRisk,
            "suggestedLotSize" to Math.round(suggestedLotSize * 100.0) / 100.0,
            "stopLossPips" to Math.round(stopLossPips * 10.0) / 10.0,
            "riskReward" to "1:${Math.round(riskReward * 10.0) / 10.0}"
        )
    }
}
