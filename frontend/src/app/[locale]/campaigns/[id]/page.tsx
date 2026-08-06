"use client";

import { FormEvent, use, useState } from "react";
import useSWR from "swr";
import { useTranslations } from "next-intl";
import { Link } from "@/i18n/navigation";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
  extractErrorMessage,
  getCampaign,
  getCreativeProofsByCampaign,
  submitCampaign,
  submitCreativeProof,
  uploadFile,
} from "@/lib/api";
import { useAuth } from "@/lib/AuthProvider";

export default function CampaignDetailPage({ params }: { params: Promise<{ id: string }> }) {
  const { id } = use(params);
  const t = useTranslations("campaigns.detail");
  const tCommon = useTranslations("common");
  const { advertiserId } = useAuth();

  const { data: campaign, error: campaignError, mutate: mutateCampaign } = useSWR(
    ["campaign", id],
    () => getCampaign(id),
  );
  const { data: proofs, mutate: mutateProofs } = useSWR(["creative-proofs", id], () =>
    getCreativeProofsByCampaign(id),
  );

  const [submittingApproval, setSubmittingApproval] = useState(false);
  const [file, setFile] = useState<File | null>(null);
  const [uploadingProof, setUploadingProof] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function handleSubmitForApproval() {
    setSubmittingApproval(true);
    setError(null);
    try {
      await submitCampaign(id);
      await mutateCampaign();
    } catch (err) {
      setError(extractErrorMessage(err, t("submitForApproval")));
    } finally {
      setSubmittingApproval(false);
    }
  }

  async function handleUploadProof(event: FormEvent) {
    event.preventDefault();
    if (!file || !advertiserId) return;
    setUploadingProof(true);
    setError(null);
    try {
      const uploaded = await uploadFile(file, advertiserId);
      const image = new Image();
      const dimensions = await new Promise<{ width: number; height: number }>((resolve) => {
        image.onload = () => resolve({ width: image.naturalWidth, height: image.naturalHeight });
        image.onerror = () => resolve({ width: 0, height: 0 });
        image.src = URL.createObjectURL(file);
      });
      await submitCreativeProof({
        campaignId: id,
        fileUrl: uploaded.publicUrl,
        width: dimensions.width,
        height: dimensions.height,
      });
      setFile(null);
      await mutateProofs();
    } catch (err) {
      setError(extractErrorMessage(err, t("uploadCreative")));
    } finally {
      setUploadingProof(false);
    }
  }

  if (campaignError) {
    return <p className="mx-auto max-w-2xl px-4 py-12 text-sm text-destructive">{campaignError.message}</p>;
  }

  if (!campaign) {
    return <p className="mx-auto max-w-2xl px-4 py-12 text-sm text-muted-foreground">{tCommon("loading")}</p>;
  }

  const canSubmit = campaign.status === "DRAFT" || campaign.status === "REJECTED";

  return (
    <div className="mx-auto flex max-w-2xl flex-col gap-6 px-4 py-12">
      <Card>
        <CardHeader>
          <CardTitle>{campaign.name}</CardTitle>
        </CardHeader>
        <CardContent className="flex flex-col gap-3 text-sm">
          <p>
            {t("status")}: <span className="font-medium">{campaign.status}</span>
          </p>
          {campaign.rejectionReason && (
            <p className="text-destructive">
              {t("rejectionReason")}: {campaign.rejectionReason}
            </p>
          )}
          {error && <p className="text-destructive">{error}</p>}
          <div className="flex flex-wrap gap-2">
            {canSubmit && (
              <Button size="sm" loading={submittingApproval} disabled={submittingApproval} onClick={handleSubmitForApproval}>
                {t("submitForApproval")}
              </Button>
            )}
            <Button asChild size="sm" variant="outline">
              <Link href={`/campaigns/${id}/contract`}>{t("requestContract")}</Link>
            </Button>
            <Button asChild size="sm" variant="outline">
              <Link href={`/campaigns/${id}/payment`}>{t("goToPayment")}</Link>
            </Button>
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle className="text-base">{t("creatives")}</CardTitle>
        </CardHeader>
        <CardContent className="flex flex-col gap-4 text-sm">
          {proofs && proofs.length === 0 && <p className="text-muted-foreground">{t("noCreatives")}</p>}
          {proofs?.map((proof) => (
            <div key={proof.id} className="rounded-md border p-3">
              <p>
                {t("version")} {proof.version} — <span className="font-medium">{proof.status}</span>
              </p>
              {proof.feedback && <p className="text-muted-foreground">{proof.feedback}</p>}
            </div>
          ))}

          <form className="flex flex-col gap-3" onSubmit={handleUploadProof}>
            <input
              type="file"
              accept="image/*"
              onChange={(event) => setFile(event.target.files?.[0] ?? null)}
              className="text-sm"
            />
            <Button type="submit" size="sm" loading={uploadingProof} disabled={uploadingProof || !file}>
              {uploadingProof ? tCommon("loading") : t("uploadCreative")}
            </Button>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
