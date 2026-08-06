"use client";

import { FormEvent, use, useMemo, useState } from "react";
import useSWR from "swr";
import { useTranslations } from "next-intl";
import { Link } from "@/i18n/navigation";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
  MediaBuyerResponse,
  PaymentMethod,
  PaymentTransactionResponse,
  completePayment,
  extractErrorMessage,
  getBooking,
  getCampaign,
  initiatePayment,
  listMediaBuyers,
} from "@/lib/api";
import { useAuth } from "@/lib/AuthProvider";

const PAYMENT_METHODS: PaymentMethod[] = ["CREDIT_CARD", "MOBILE_MONEY", "BANK_TRANSFER", "CREDIT_ACCOUNT"];

export default function CampaignPaymentPage({ params }: { params: Promise<{ id: string }> }) {
  const { id } = use(params);
  const t = useTranslations("payments");
  const tCommon = useTranslations("common");
  const { advertiserId } = useAuth();

  const { data: campaign } = useSWR(["campaign", id], () => getCampaign(id));
  const { data: booking } = useSWR(
    campaign ? ["booking", campaign.bookingId] : null,
    ([, bookingId]) => getBooking(bookingId),
  );

  const [amount, setAmount] = useState("");
  const [method, setMethod] = useState<PaymentMethod>("CREDIT_CARD");
  const [delegate, setDelegate] = useState(false);
  const [mediaBuyerQuery, setMediaBuyerQuery] = useState("");
  const [selectedMediaBuyer, setSelectedMediaBuyer] = useState<MediaBuyerResponse | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [payment, setPayment] = useState<PaymentTransactionResponse | null>(null);
  const [gatewayReference, setGatewayReference] = useState("");
  const [completing, setCompleting] = useState(false);

  const { data: mediaBuyers } = useSWR(delegate ? "media-buyers" : null, listMediaBuyers);

  const filteredMediaBuyers = useMemo(() => {
    if (!mediaBuyers) return [];
    const query = mediaBuyerQuery.trim().toLowerCase();
    if (!query) return mediaBuyers;
    return mediaBuyers.filter(
      (buyer) =>
        buyer.companyName.toLowerCase().includes(query) || buyer.contactEmail.toLowerCase().includes(query),
    );
  }, [mediaBuyers, mediaBuyerQuery]);

  async function handleInitiate(event: FormEvent) {
    event.preventDefault();
    if (!advertiserId || !campaign) return;
    const payerId = delegate ? selectedMediaBuyer?.id : advertiserId;
    if (!payerId || !booking) return;
    setSubmitting(true);
    setError(null);
    try {
      const result = await initiatePayment({
        payerId,
        referenceId: campaign.id,
        amount,
        currency: booking.currency,
        paymentMethod: method,
      });
      setPayment(result);
    } catch (err) {
      setError(extractErrorMessage(err, t("title")));
    } finally {
      setSubmitting(false);
    }
  }

  async function handleComplete(event: FormEvent) {
    event.preventDefault();
    if (!payment) return;
    setCompleting(true);
    setError(null);
    try {
      await completePayment(payment.id, { gatewayReference });
      setPayment({ ...payment, status: "SUCCESSFUL", gatewayReference });
    } catch (err) {
      setError(extractErrorMessage(err, t("complete")));
    } finally {
      setCompleting(false);
    }
  }

  if (!campaign || !booking) {
    return <p className="mx-auto max-w-2xl px-4 py-12 text-sm text-muted-foreground">{tCommon("loading")}</p>;
  }

  return (
    <div className="mx-auto flex max-w-2xl flex-col gap-6 px-4 py-12">
      <Link href={`/campaigns/${id}`} className="text-sm text-muted-foreground hover:underline">
        ← {campaign.name}
      </Link>

      <Card>
        <CardHeader>
          <CardTitle>{t("title")}</CardTitle>
        </CardHeader>
        <CardContent className="flex flex-col gap-4 text-sm">
          {error && <p className="text-destructive">{error}</p>}

          {!payment ? (
            <form className="flex flex-col gap-4" onSubmit={handleInitiate}>
              <Input
                name="amount"
                type="number"
                step="0.01"
                label={`${t("amount")} (${booking.currency})`}
                value={amount}
                onChange={(event) => setAmount(event.target.value)}
                required
              />

              <div className="flex flex-col gap-1.5">
                <label className="text-sm font-medium" htmlFor="method">
                  {t("method")}
                </label>
                <select
                  id="method"
                  className="h-10 rounded-md border bg-transparent px-3 text-sm"
                  value={method}
                  onChange={(event) => setMethod(event.target.value as PaymentMethod)}
                >
                  {PAYMENT_METHODS.map((value) => (
                    <option key={value} value={value}>
                      {value}
                    </option>
                  ))}
                </select>
              </div>

              <div className="flex gap-2">
                <Button
                  type="button"
                  size="sm"
                  variant={!delegate ? "default" : "outline"}
                  onClick={() => setDelegate(false)}
                >
                  {t("payMyself")}
                </Button>
                <Button
                  type="button"
                  size="sm"
                  variant={delegate ? "default" : "outline"}
                  onClick={() => setDelegate(true)}
                >
                  {t("delegate")}
                </Button>
              </div>

              {delegate && (
                <div className="flex flex-col gap-2">
                  <Input
                    name="mediaBuyerQuery"
                    label={t("searchMediaBuyer")}
                    value={mediaBuyerQuery}
                    onChange={(event) => {
                      setMediaBuyerQuery(event.target.value);
                      setSelectedMediaBuyer(null);
                    }}
                  />
                  <div className="flex flex-col gap-1">
                    {filteredMediaBuyers.length === 0 && (
                      <p className="text-muted-foreground">{t("noMediaBuyer")}</p>
                    )}
                    {filteredMediaBuyers.map((buyer) => (
                      <button
                        type="button"
                        key={buyer.id}
                        onClick={() => setSelectedMediaBuyer(buyer)}
                        className={`rounded-md border p-2 text-left ${
                          selectedMediaBuyer?.id === buyer.id ? "border-primary bg-accent" : ""
                        }`}
                      >
                        <p className="font-medium">{buyer.companyName}</p>
                        <p className="text-xs text-muted-foreground">{buyer.contactEmail}</p>
                      </button>
                    ))}
                  </div>
                </div>
              )}

              <Button
                type="submit"
                loading={submitting}
                disabled={submitting || (delegate && !selectedMediaBuyer)}
              >
                {submitting ? t("initiating") : t("initiate")}
              </Button>
            </form>
          ) : (
            <div className="flex flex-col gap-4">
              <p>
                {t("status")}: <span className="font-medium">{payment.status}</span>
              </p>
              <p>
                {t("amount")}: {payment.amount} {payment.currency}
              </p>
              {delegate && selectedMediaBuyer && (
                <p>
                  {t("delegatedTo")}: {selectedMediaBuyer.companyName}
                </p>
              )}

              {payment.status === "PENDING" && (
                <form className="flex flex-col gap-3" onSubmit={handleComplete}>
                  <Input
                    name="gatewayReference"
                    label={t("gatewayReference")}
                    value={gatewayReference}
                    onChange={(event) => setGatewayReference(event.target.value)}
                    required
                  />
                  <Button type="submit" size="sm" loading={completing} disabled={completing}>
                    {completing ? t("completing") : t("complete")}
                  </Button>
                </form>
              )}
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
