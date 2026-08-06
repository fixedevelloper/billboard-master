"use client";

import { FormEvent, useState } from "react";
import useSWR from "swr";
import { useTranslations } from "next-intl";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { completePayment, extractErrorMessage, getMediaBuyerByUserId, getPaymentsByPayer } from "@/lib/api";
import { useAuth } from "@/lib/AuthProvider";

export default function MediaBuyerPaymentsPage() {
  const t = useTranslations("mediaBuyerPayments");
  const tPayments = useTranslations("payments");
  const tCommon = useTranslations("common");
  const { userId } = useAuth();

  const { data: mediaBuyer, error: mediaBuyerError } = useSWR(
    userId ? ["media-buyer-by-user", userId] : null,
    ([, id]) => getMediaBuyerByUserId(id),
  );

  const {
    data: payments,
    mutate,
  } = useSWR(mediaBuyer ? ["payments-by-payer", mediaBuyer.id] : null, ([, payerId]) =>
    getPaymentsByPayer(payerId),
  );

  const [gatewayReferences, setGatewayReferences] = useState<Record<string, string>>({});
  const [completingId, setCompletingId] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  async function handleComplete(event: FormEvent, paymentId: string) {
    event.preventDefault();
    setCompletingId(paymentId);
    setError(null);
    try {
      await completePayment(paymentId, { gatewayReference: gatewayReferences[paymentId] ?? "" });
      await mutate();
    } catch (err) {
      setError(extractErrorMessage(err, t("complete")));
    } finally {
      setCompletingId(null);
    }
  }

  if (mediaBuyerError) {
    return <p className="mx-auto max-w-2xl px-4 py-12 text-sm text-muted-foreground">{t("notMediaBuyer")}</p>;
  }

  return (
    <div className="mx-auto flex max-w-2xl flex-col gap-6 px-4 py-12">
      <h1 className="text-2xl font-semibold">{t("title")}</h1>

      {error && <p className="text-sm text-destructive">{error}</p>}
      {!mediaBuyer && <p className="text-sm text-muted-foreground">{tCommon("loading")}</p>}
      {payments && payments.length === 0 && <p className="text-sm text-muted-foreground">{t("empty")}</p>}

      <div className="flex flex-col gap-4">
        {payments?.map((payment) => (
          <Card key={payment.id}>
            <CardHeader>
              <CardTitle className="text-base">
                {payment.amount} {payment.currency}
              </CardTitle>
            </CardHeader>
            <CardContent className="flex flex-col gap-3 text-sm">
              <p>
                {tPayments("status")}: <span className="font-medium">{payment.status}</span>
              </p>
              {payment.status === "PENDING" && (
                <form className="flex items-end gap-2" onSubmit={(event) => handleComplete(event, payment.id)}>
                  <Input
                    name={`gatewayReference-${payment.id}`}
                    label={tPayments("gatewayReference")}
                    value={gatewayReferences[payment.id] ?? ""}
                    onChange={(event) =>
                      setGatewayReferences((prev) => ({ ...prev, [payment.id]: event.target.value }))
                    }
                    required
                  />
                  <Button
                    type="submit"
                    size="sm"
                    loading={completingId === payment.id}
                    disabled={completingId === payment.id}
                  >
                    {t("complete")}
                  </Button>
                </form>
              )}
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  );
}
