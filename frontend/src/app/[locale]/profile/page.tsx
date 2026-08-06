"use client";

import { useEffect } from "react";
import useSWR from "swr";
import { useTranslations } from "next-intl";
import { Link, useRouter } from "@/i18n/navigation";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { getUserProfile } from "@/lib/api";
import { useAuth } from "@/lib/AuthProvider";

export default function ProfilePage() {
  const t = useTranslations("auth.profile");
  const tCommon = useTranslations("common");
  const router = useRouter();
  const { userId, advertiserId, isAuthenticated, hydrated, logout } = useAuth();

  useEffect(() => {
    if (hydrated && !isAuthenticated) {
      router.replace("/login");
    }
  }, [hydrated, isAuthenticated, router]);

  const { data: profile, error } = useSWR(
    userId ? ["profile", userId] : null,
    ([, id]) => getUserProfile(id),
  );

  if (!hydrated || !isAuthenticated) {
    return null;
  }

  return (
    <div className="mx-auto flex max-w-md flex-col gap-6 px-4 py-16">
      <Card>
        <CardHeader>
          <CardTitle>{t("title")}</CardTitle>
        </CardHeader>
        <CardContent className="flex flex-col gap-4">
          {error && <p className="text-sm text-destructive">{t("loadError")}</p>}
          {!profile && !error && <p className="text-sm text-muted-foreground">{tCommon("loading")}</p>}
          {profile && (
            <dl className="flex flex-col gap-3 text-sm">
              <div>
                <dt className="text-muted-foreground">{t("fullName")}</dt>
                <dd className="font-medium">{profile.fullName}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">{t("email")}</dt>
                <dd className="font-medium">{profile.email}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">{t("phone")}</dt>
                <dd className="font-medium">{profile.phoneNumber ?? t("notProvided")}</dd>
              </div>
              <div>
                <dt className="text-muted-foreground">{t("status")}</dt>
                <dd className="font-medium">{profile.status}</dd>
              </div>
            </dl>
          )}
          <Button asChild variant="secondary">
            <Link href={advertiserId ? "/billboards" : "/become-advertiser"}>
              {advertiserId ? t("advertiserProfile") : t("becomeAdvertiser")}
            </Link>
          </Button>
          <Button asChild variant="secondary">
            <Link href="/my-billboards">{t("ownerProfile")}</Link>
          </Button>
          <Button variant="outline" onClick={logout}>
            {t("logout")}
          </Button>
        </CardContent>
      </Card>
    </div>
  );
}
