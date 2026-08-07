"use client";

import { useMemo, useState } from "react";
import useSWR from "swr";
import { useTranslations } from "next-intl";
import {
  Radar,
  Calendar,
  User,
  Check,
  X,
  AlertCircle,
  Loader2,
  CalendarDays,
  Building2,
  ShieldAlert,
} from "lucide-react";
import { Link } from "@/i18n/navigation";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Skeleton } from "@/components/ui/skeleton";
import { StatusBadge } from "@/components/admin/StatusBadge";
import { usePayerMap } from "@/components/admin/usePayerMap";
import {
  cancelBooking,
  confirmBooking,
  extractErrorMessage,
  getBillboardsByOwner,
  getBookingsByBillboard,
  BookingResponse,
} from "@/lib/api";
import { useAuth } from "@/lib/AuthProvider";

// Helper de formatage monétaire
function formatCurrency(amount: number, currency: string) {
  try {
    return new Intl.NumberFormat("fr-FR", {
      style: "currency",
      currency: currency || "EUR",
      maximumFractionDigits: 2,
    }).format(amount);
  } catch {
    return `${amount} ${currency}`;
  }
}

export default function OwnerBookingsPage() {
  const t = useTranslations("ownerBookings");
  const tTracking = useTranslations("bookingTracking");
  const tCommon = useTranslations("common");
  const { ownerId } = useAuth();

  const { data: billboards, isLoading: isLoadingBillboards } = useSWR(
      ownerId ? ["my-billboards", ownerId] : null,
      () => getBillboardsByOwner(ownerId as string)
  );

  const billboardIds = useMemo(
      () => billboards?.map((b) => b.id) ?? [],
      [billboards]
  );

  const {
    data: bookings,
    mutate,
    isLoading: isLoadingBookings,
  } = useSWR(
      billboardIds.length > 0 ? ["owner-bookings", billboardIds.join(",")] : null,
      () =>
          Promise.all(
              billboardIds.map((id) => getBookingsByBillboard(id))
          ).then((lists) => lists.flat())
  );

  const billboardMap = useMemo(
      () => new Map(billboards?.map((b) => [b.id, b.title]) ?? []),
      [billboards]
  );
  const payerMap = usePayerMap();

  const [runningId, setRunningId] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const isLoading = isLoadingBillboards || (billboardIds.length > 0 && isLoadingBookings);

  async function handleConfirm(id: string) {
    setRunningId(id);
    setError(null);
    try {
      await confirmBooking(id);
      await mutate();
    } catch (err) {
      setError(extractErrorMessage(err, t("confirm")));
    } finally {
      setRunningId(null);
    }
  }

  async function handleCancel(id: string) {
    setRunningId(id);
    setError(null);
    try {
      await cancelBooking(id);
      await mutate();
    } catch (err) {
      setError(extractErrorMessage(err, t("cancel")));
    } finally {
      setRunningId(null);
    }
  }

  // Si l'utilisateur n'est pas identifié comme propriétaire
  if (!ownerId) {
    return (
        <div className="mx-auto flex max-w-md flex-col items-center justify-center px-4 py-20 text-center">
          <div className="flex h-12 w-12 items-center justify-center rounded-2xl bg-amber-500/10 text-amber-600 dark:text-amber-400 mb-4 border border-amber-500/20">
            <ShieldAlert className="h-6 w-6" />
          </div>
          <h2 className="text-lg font-bold text-foreground">Accès restreint</h2>
          <p className="mt-1 text-xs text-muted-foreground leading-relaxed">
            Vous devez être connecté en tant que propriétaire pour consulter les réservations.
          </p>
        </div>
    );
  }

  return (
      <div className="mx-auto flex max-w-6xl flex-col gap-6 px-4 py-8 md:px-6 md:py-12">
        {/* En-tête de la page */}
        <div className="flex flex-col gap-1 border-b border-border/60 pb-5">
          <div className="flex items-center gap-2 text-xs font-semibold text-emerald-600 dark:text-emerald-400 uppercase tracking-wider">
            <CalendarDays className="h-4 w-4" />
            <span>Espace Propriétaire</span>
          </div>
          <h1 className="text-2xl font-extrabold tracking-tight text-foreground sm:text-3xl">
            {t("title")}
          </h1>
          <p className="text-xs text-muted-foreground">
            Gérez les demandes de réservation sur vos panneaux publicitaires et suivez leur état.
          </p>
        </div>

        {/* Message d'erreur */}
        {error && (
            <div className="flex items-center gap-2.5 rounded-xl border border-destructive/30 bg-destructive/10 p-3.5 text-xs font-medium text-destructive">
              <AlertCircle className="h-4 w-4 shrink-0" />
              <span>{error}</span>
            </div>
        )}

        {/* Carte principale de contenu */}
        <Card className="border-border/60 shadow-sm overflow-hidden">
          {/* Chargement (Skeleton UI) */}
          {isLoading && (
              <div className="p-6 space-y-4">
                <Skeleton className="h-8 w-full rounded-xl" />
                <Skeleton className="h-12 w-full rounded-xl" />
                <Skeleton className="h-12 w-full rounded-xl" />
                <Skeleton className="h-12 w-full rounded-xl" />
              </div>
          )}

          {/* État vide */}
          {!isLoading && bookings && bookings.length === 0 && (
              <div className="flex flex-col items-center justify-center rounded-2xl bg-muted/10 p-12 text-center">
                <div className="flex h-12 w-12 items-center justify-center rounded-2xl bg-muted text-muted-foreground mb-3">
                  <CalendarDays className="h-6 w-6" />
                </div>
                <h3 className="text-sm font-bold text-foreground">{t("empty")}</h3>
                <p className="text-xs text-muted-foreground mt-1 max-w-sm">
                  Aucune réservation enregistrée pour vos panneaux publicitaires actuellement.
                </p>
              </div>
          )}

          {/* Tableau des réservations */}
          {!isLoading && bookings && bookings.length > 0 && (
              <div className="overflow-x-auto">
                <Table>
                  <TableHeader className="bg-muted/40">
                    <TableRow className="hover:bg-transparent border-border/60">
                      <TableHead className="text-xs font-bold">{t("colBillboard")}</TableHead>
                      <TableHead className="text-xs font-bold">{t("colAdvertiser")}</TableHead>
                      <TableHead className="text-xs font-bold">{t("colDates")}</TableHead>
                      <TableHead className="text-xs font-bold">{t("colTotal")}</TableHead>
                      <TableHead className="text-xs font-bold">{t("colStatus")}</TableHead>
                      <TableHead className="text-xs font-bold text-right">{t("colActions")}</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {bookings.map((booking: BookingResponse) => {
                      const isActionRunning = runningId === booking.id;
                      const isPending = booking.status === "PENDING";
                      const isCancelable =
                          booking.status !== "CANCELLED" && booking.status !== "COMPLETED";

                      return (
                          <TableRow key={booking.id} className="border-border/40 hover:bg-muted/20">
                            {/* Nom / ID du panneau */}
                            <TableCell className="font-semibold text-xs text-foreground">
                              <div className="flex items-center gap-2">
                                <Building2 className="h-3.5 w-3.5 text-muted-foreground shrink-0" />
                                <span className="truncate max-w-[180px]">
                            {billboardMap.get(booking.billboardId) ?? booking.billboardId}
                          </span>
                              </div>
                            </TableCell>

                            {/* Nom / ID de l'annonceur */}
                            <TableCell className="text-xs text-muted-foreground">
                              <div className="flex items-center gap-1.5">
                                <User className="h-3.5 w-3.5 text-muted-foreground/70 shrink-0" />
                                <span className="truncate max-w-[150px]">
                            {payerMap.get(booking.advertiserId) ?? booking.advertiserId}
                          </span>
                              </div>
                            </TableCell>

                            {/* Dates de début et fin */}
                            <TableCell className="text-xs font-mono text-muted-foreground whitespace-nowrap">
                              <div className="flex items-center gap-1.5">
                                <Calendar className="h-3.5 w-3.5 text-muted-foreground/70 shrink-0" />
                                <span>
                            {booking.startDate} → {booking.endDate}
                          </span>
                              </div>
                            </TableCell>

                            {/* Montant total */}
                            <TableCell className="text-xs font-bold text-foreground whitespace-nowrap">
                              {formatCurrency(Number(booking.totalPrice), booking.currency)}
                            </TableCell>

                            {/* Badge de Statut */}
                            <TableCell className="whitespace-nowrap">
                              <StatusBadge status={booking.status} />
                            </TableCell>

                            {/* Actions disponibles */}
                            <TableCell className="text-right whitespace-nowrap">
                              <div className="flex items-center justify-end gap-1.5">
                                {/* Suivi / Tracking */}
                                <Button
                                    asChild
                                    size="sm"
                                    variant="ghost"
                                    className="h-8 gap-1 text-xs hover:bg-muted"
                                >
                                  <Link href={`/my-billboards/bookings/${booking.id}`}>
                                    <Radar className="h-3.5 w-3.5 text-emerald-600 dark:text-emerald-400" />
                                    <span className="hidden sm:inline">{tTracking("trigger")}</span>
                                  </Link>
                                </Button>

                                {/* Action : Confirmer */}
                                {isPending && (
                                    <Button
                                        size="sm"
                                        variant="outline"
                                        disabled={runningId !== null}
                                        onClick={() => handleConfirm(booking.id)}
                                        className="h-8 gap-1 text-xs border-emerald-500/30 text-emerald-600 hover:bg-emerald-500/10 hover:text-emerald-700 dark:text-emerald-400 dark:hover:bg-emerald-500/20"
                                    >
                                      {isActionRunning ? (
                                          <Loader2 className="h-3.5 w-3.5 animate-spin" />
                                      ) : (
                                          <Check className="h-3.5 w-3.5" />
                                      )}
                                      <span>{t("confirm")}</span>
                                    </Button>
                                )}

                                {/* Action : Annuler */}
                                {isCancelable && (
                                    <Button
                                        size="sm"
                                        variant="outline"
                                        disabled={runningId !== null}
                                        onClick={() => handleCancel(booking.id)}
                                        className="h-8 gap-1 text-xs border-destructive/30 text-destructive hover:bg-destructive/10 hover:text-destructive dark:hover:bg-destructive/20"
                                    >
                                      {isActionRunning ? (
                                          <Loader2 className="h-3.5 w-3.5 animate-spin" />
                                      ) : (
                                          <X className="h-3.5 w-3.5" />
                                      )}
                                      <span>{t("cancel")}</span>
                                    </Button>
                                )}
                              </div>
                            </TableCell>
                          </TableRow>
                      );
                    })}
                  </TableBody>
                </Table>
              </div>
          )}
        </Card>
      </div>
  );
}