"use client";

import { useEffect } from "react";
import useSWR from "swr";
import { useTranslations } from "next-intl";
import { Link, useRouter } from "@/i18n/navigation";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { cancelBooking, confirmBooking, getBookingsByAdvertiser } from "@/lib/api";
import { useAuth } from "@/lib/AuthProvider";

export default function BookingsPage() {
  const t = useTranslations("bookings");
  const tCommon = useTranslations("common");
  const router = useRouter();
  const { advertiserId, hydrated } = useAuth();

  useEffect(() => {
    if (hydrated && !advertiserId) {
      router.replace("/become-advertiser");
    }
  }, [hydrated, advertiserId, router]);

  const { data: bookings, error, mutate } = useSWR(
    advertiserId ? ["bookings", advertiserId] : null,
    ([, id]) => getBookingsByAdvertiser(id),
  );

  if (!hydrated || !advertiserId) {
    return null;
  }

  return (
    <div className="mx-auto flex max-w-3xl flex-col gap-6 px-4 py-12">
      <h1 className="text-2xl font-semibold">{t("title")}</h1>

      {error && <p className="text-sm text-destructive">{error.message}</p>}
      {!bookings && !error && <p className="text-sm text-muted-foreground">{tCommon("loading")}</p>}
      {bookings && bookings.length === 0 && <p className="text-sm text-muted-foreground">{t("empty")}</p>}

      <div className="flex flex-col gap-4">
        {bookings?.map((booking) => (
          <Card key={booking.id}>
            <CardHeader>
              <CardTitle className="text-base">
                {t("dates")}: {booking.startDate} → {booking.endDate}
              </CardTitle>
            </CardHeader>
            <CardContent className="flex flex-col gap-3 text-sm">
              <p>
                {t("status")}: <span className="font-medium">{booking.status}</span>
              </p>
              <p>
                {t("total")}: {booking.totalPrice} {booking.currency}
              </p>
              <div className="flex flex-wrap gap-2">
                {booking.status === "PENDING" && (
                  <Button
                    size="sm"
                    variant="outline"
                    onClick={() => confirmBooking(booking.id).then(() => mutate())}
                  >
                    {t("confirm")}
                  </Button>
                )}
                {booking.status !== "CANCELLED" && booking.status !== "COMPLETED" && (
                  <Button
                    size="sm"
                    variant="outline"
                    onClick={() => cancelBooking(booking.id).then(() => mutate())}
                  >
                    {t("cancel")}
                  </Button>
                )}
                <Button asChild size="sm">
                  <Link href={`/campaigns/new?bookingId=${booking.id}`}>{t("createCampaign")}</Link>
                </Button>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  );
}
