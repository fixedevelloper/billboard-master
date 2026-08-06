"use client";

import { FormEvent, use, useState } from "react";
import useSWR from "swr";
import { useTranslations } from "next-intl";
import { Link, useRouter } from "@/i18n/navigation";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { createBooking, extractErrorMessage, getBillboard } from "@/lib/api";
import { useAuth } from "@/lib/AuthProvider";

export default function BillboardDetailPage({ params }: { params: Promise<{ id: string }> }) {
  const { id } = use(params);
  const tDetail = useTranslations("billboards.detail");
  const tBooking = useTranslations("billboards.booking");
  const tCommon = useTranslations("common");
  const router = useRouter();
  const { advertiserId } = useAuth();

  const { data: billboard, error: loadError } = useSWR(["billboard", id], () => getBillboard(id));

  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    if (!billboard || !advertiserId) return;
    setSubmitting(true);
    setError(null);
    try {
      await createBooking({
        billboardId: billboard.id,
        advertiserId,
        startDate,
        endDate,
        dailyRate: billboard.dailyRate,
        currency: billboard.currency,
      });
      setSuccess(true);
    } catch (err) {
      setError(extractErrorMessage(err, tBooking("title")));
    } finally {
      setSubmitting(false);
    }
  }

  if (loadError) {
    return <p className="mx-auto max-w-2xl px-4 py-12 text-sm text-destructive">{loadError.message}</p>;
  }

  if (!billboard) {
    return <p className="mx-auto max-w-2xl px-4 py-12 text-sm text-muted-foreground">{tCommon("loading")}</p>;
  }

  return (
    <div className="mx-auto flex max-w-2xl flex-col gap-6 px-4 py-12">
      <Link href="/billboards" className="text-sm text-muted-foreground hover:underline">
        ← {tDetail("back")}
      </Link>

      <Card>
        <CardHeader>
          <CardTitle>{billboard.title}</CardTitle>
        </CardHeader>
        <CardContent className="flex flex-col gap-2 text-sm">
          <p>{billboard.description}</p>
          <dl className="grid grid-cols-2 gap-2">
            <div>
              <dt className="text-muted-foreground">{tDetail("type")}</dt>
              <dd>{billboard.type}</dd>
            </div>
            <div>
              <dt className="text-muted-foreground">{tDetail("city")}</dt>
              <dd>{billboard.city}</dd>
            </div>
            <div>
              <dt className="text-muted-foreground">{tDetail("address")}</dt>
              <dd>{billboard.address}</dd>
            </div>
            <div>
              <dt className="text-muted-foreground">{tDetail("status")}</dt>
              <dd>{billboard.status}</dd>
            </div>
            <div>
              <dt className="text-muted-foreground">{tDetail("dailyRate")}</dt>
              <dd>
                {billboard.dailyRate} {billboard.currency}
              </dd>
            </div>
          </dl>
        </CardContent>
      </Card>

      {!advertiserId ? (
        <p className="text-sm text-muted-foreground">
          {tDetail("needAdvertiser")}{" "}
          <Link href="/become-advertiser" className="font-medium text-primary underline-offset-4 hover:underline">
            {tDetail("bookCta")}
          </Link>
        </p>
      ) : (
        <Card>
          <CardHeader>
            <CardTitle className="text-base">{tBooking("title")}</CardTitle>
          </CardHeader>
          <CardContent>
            {success ? (
              <div className="flex flex-col gap-4">
                <p className="text-sm text-foreground">{tBooking("success")}</p>
                <Button onClick={() => router.push("/bookings")}>{tBooking("viewBooking")}</Button>
              </div>
            ) : (
              <form className="flex flex-col gap-4" onSubmit={handleSubmit}>
                <div className="flex gap-3">
                  <Input
                    name="startDate"
                    type="date"
                    label={tBooking("startDate")}
                    value={startDate}
                    onChange={(event) => setStartDate(event.target.value)}
                    required
                  />
                  <Input
                    name="endDate"
                    type="date"
                    label={tBooking("endDate")}
                    value={endDate}
                    onChange={(event) => setEndDate(event.target.value)}
                    required
                  />
                </div>
                {error && <p className="text-sm text-destructive">{error}</p>}
                <Button type="submit" loading={submitting} disabled={submitting}>
                  {submitting ? tBooking("submitting") : tBooking("submit")}
                </Button>
              </form>
            )}
          </CardContent>
        </Card>
      )}
    </div>
  );
}
