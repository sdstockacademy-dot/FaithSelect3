package com.faithselect.data.repository

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import com.faithselect.BuildConfig
import com.faithselect.domain.model.SubscriptionStatus
import com.faithselect.domain.repository.SubscriptionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Google Play Billing subscription implementation.
 *
 * SUBSCRIPTION PRODUCT ID: faith_select_monthly_99
 * ─ Set this up in Google Play Console → Monetize → Subscriptions
 * ─ Price: ₹99/month
 * ─ Free trial: 3 days
 * ─ Grace period: 3 days (recommended)
 *
 * TESTING:
 * 1. Add test accounts in Play Console → License Testing
 * 2. Use test product IDs during development
 * 3. Use BillingClient in debug builds with test purchases
 */
@Singleton
class SubscriptionRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SubscriptionRepository, PurchasesUpdatedListener {

    // ─── Subscription product ID — must match Play Console exactly ────────────
    val SUBSCRIPTION_ID = "faith_select_monthly_99"

    private val _subscriptionStatus = MutableStateFlow(SubscriptionStatus())
    private var billingClient: BillingClient? = null

    // Expose subscription status as a Flow
    override fun getSubscriptionStatus(): Flow<SubscriptionStatus> =
        _subscriptionStatus.asStateFlow()

    /**
     * Initialize the BillingClient and connect it.
     * Call from Application class or ViewModel initialization.
     */
    fun initialize() {
        billingClient = BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases(
                PendingPurchasesParams.newBuilder()
                    .enableOneTimeProducts()
                    .build()
            )
            .build()

        connectBillingClient()
    }

    private fun connectBillingClient() {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    // Billing is ready — check existing purchases
                    queryExistingPurchases()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Retry connection
                connectBillingClient()
            }
        })
    }

    /**
     * Query existing purchases to restore subscription state.
     * Called on app start and after billing setup.
     */
    private fun queryExistingPurchases() {
        billingClient?.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        ) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                handlePurchases(purchases)
            }
        }
    }

    /**
     * Launch the subscription purchase flow.
     * Call from the Paywall screen Activity context.
     */
    suspend fun launchSubscriptionFlow(activity: Activity): BillingResult {
        // First, query available products
        val productDetails = getProductDetails() ?: return BillingResult.newBuilder()
            .setResponseCode(BillingClient.BillingResponseCode.ERROR)
            .build()

        val offerToken = productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken
            ?: return BillingResult.newBuilder()
                .setResponseCode(BillingClient.BillingResponseCode.ITEM_UNAVAILABLE)
                .build()

        val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(productDetails)
            .setOfferToken(offerToken)
            .build()

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productDetailsParams))
            .build()

        return billingClient?.launchBillingFlow(activity, billingFlowParams)
            ?: BillingResult.newBuilder()
                .setResponseCode(BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE)
                .build()
    }

    /**
     * Query product details for the subscription (used to show price info).
     */
    suspend fun getProductDetails(): ProductDetails? = suspendCancellableCoroutine { cont ->
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(SUBSCRIPTION_ID)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient?.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                cont.resume(productDetailsList.firstOrNull())
            } else {
                cont.resume(null)
            }
        } ?: cont.resume(null)
    }

    /**
     * Called automatically when a purchase is updated (new purchase or restored).
     */
    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchases?.let { handlePurchases(it) }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                // User cancelled — no action needed
            }
            else -> {
                // Handle other errors
            }
        }
    }

    /**
     * Process a list of purchases and update subscription status.
     */
    private fun handlePurchases(purchases: List<Purchase>) {
        val activePurchase = purchases.firstOrNull { purchase ->
            purchase.purchaseState == Purchase.PurchaseState.PURCHASED &&
                    purchase.products.contains(SUBSCRIPTION_ID)
        }

        if (activePurchase != null) {
            // Acknowledge the purchase if not already done
            if (!activePurchase.isAcknowledged) {
                acknowledgePurchaseInternal(activePurchase.purchaseToken)
            }

            _subscriptionStatus.value = SubscriptionStatus(
                isSubscribed = true,
                isTrialActive = activePurchase.isAutoRenewing,
                purchaseToken = activePurchase.purchaseToken,
                productId = SUBSCRIPTION_ID
            )
        } else {
            _subscriptionStatus.value = SubscriptionStatus(isSubscribed = false)
        }
    }

    override suspend fun refreshSubscriptionStatus() {
        queryExistingPurchases()
    }

    override suspend fun acknowledgePurchase(purchaseToken: String) {
        acknowledgePurchaseInternal(purchaseToken)
    }

    private fun acknowledgePurchaseInternal(purchaseToken: String) {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()

        billingClient?.acknowledgePurchase(params) { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                // Purchase acknowledged successfully
            }
        }
    }

    fun cleanup() {
        billingClient?.endConnection()
    }
}
