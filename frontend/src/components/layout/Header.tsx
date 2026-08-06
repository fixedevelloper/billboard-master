"use client";

import { useTranslations } from "next-intl";
import { Link } from "@/i18n/navigation";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/lib/AuthProvider";

export function Header() {
  const t = useTranslations("nav");
  const { isAuthenticated, hydrated, logout } = useAuth();

  return (
    <header className="border-b bg-background">
      <div className="mx-auto flex h-16 max-w-6xl flex-wrap items-center justify-between gap-2 px-4">
        <Link href="/" className="text-lg font-semibold">
          Billboard
        </Link>
        <nav className="flex flex-wrap items-center gap-1">
          {!hydrated ? null : isAuthenticated ? (
            <>
              <Button asChild variant="ghost" size="sm">
                <Link href="/billboards">{t("billboards")}</Link>
              </Button>
              <Button asChild variant="ghost" size="sm">
                <Link href="/bookings">{t("bookings")}</Link>
              </Button>
              <Button asChild variant="ghost" size="sm">
                <Link href="/campaigns">{t("campaigns")}</Link>
              </Button>
              <Button asChild variant="ghost" size="sm">
                <Link href="/my-billboards">{t("myBillboards")}</Link>
              </Button>
              <Button asChild variant="ghost" size="sm">
                <Link href="/media-buyer/payments">{t("mediaBuyerPayments")}</Link>
              </Button>
              <Button asChild variant="ghost" size="sm">
                <Link href="/profile">{t("profile")}</Link>
              </Button>
              <Button variant="outline" size="sm" onClick={logout}>
                {t("logout")}
              </Button>
            </>
          ) : (
            <>
              <Button asChild variant="ghost">
                <Link href="/login">{t("login")}</Link>
              </Button>
              <Button asChild>
                <Link href="/register">{t("register")}</Link>
              </Button>
            </>
          )}
        </nav>
      </div>
    </header>
  );
}
