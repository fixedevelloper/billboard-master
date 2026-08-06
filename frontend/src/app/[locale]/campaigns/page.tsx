"use client";

import useSWR from "swr";
import { useTranslations } from "next-intl";
import { Link } from "@/i18n/navigation";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { getCampaignsByAdvertiser } from "@/lib/api";
import { useAuth } from "@/lib/AuthProvider";

export default function CampaignsListPage() {
  const t = useTranslations("campaigns.list");
  const tCommon = useTranslations("common");
  const { advertiserId } = useAuth();

  const { data: campaigns, error } = useSWR(
    advertiserId ? ["campaigns", advertiserId] : null,
    ([, id]) => getCampaignsByAdvertiser(id),
  );

  return (
    <div className="mx-auto flex max-w-3xl flex-col gap-6 px-4 py-12">
      <h1 className="text-2xl font-semibold">{t("title")}</h1>

      {error && <p className="text-sm text-destructive">{error.message}</p>}
      {!campaigns && !error && <p className="text-sm text-muted-foreground">{tCommon("loading")}</p>}
      {campaigns && campaigns.length === 0 && <p className="text-sm text-muted-foreground">{t("empty")}</p>}

      <div className="flex flex-col gap-4">
        {campaigns?.map((campaign) => (
          <Link key={campaign.id} href={`/campaigns/${campaign.id}`}>
            <Card className="transition-colors hover:bg-accent">
              <CardHeader>
                <CardTitle className="text-base">{campaign.name}</CardTitle>
              </CardHeader>
              <CardContent className="text-sm text-muted-foreground">
                {t("status")}: {campaign.status}
              </CardContent>
            </Card>
          </Link>
        ))}
      </div>
    </div>
  );
}
