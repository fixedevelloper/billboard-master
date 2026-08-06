"use client";

import { FormEvent, Suspense, useState } from "react";
import { useSearchParams } from "next/navigation";
import { useTranslations } from "next-intl";
import { useRouter } from "@/i18n/navigation";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { createCampaign, extractErrorMessage, uploadFile } from "@/lib/api";
import { useAuth } from "@/lib/AuthProvider";

export default function NewCampaignPage() {
  return (
    <Suspense fallback={null}>
      <NewCampaignForm />
    </Suspense>
  );
}

function NewCampaignForm() {
  const t = useTranslations("campaigns.create");
  const router = useRouter();
  const searchParams = useSearchParams();
  const bookingId = searchParams.get("bookingId");
  const { advertiserId } = useAuth();

  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [file, setFile] = useState<File | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    if (!advertiserId || !bookingId) return;
    setSubmitting(true);
    setError(null);
    try {
      let mediaUrl: string | undefined;
      let fileType: string | undefined;
      let fileSize: number | undefined;
      if (file) {
        const uploaded = await uploadFile(file, advertiserId);
        mediaUrl = uploaded.publicUrl;
        fileType = file.type;
        fileSize = file.size;
      }
      const campaign = await createCampaign({
        bookingId,
        advertiserId,
        name,
        description: description || undefined,
        mediaUrl,
        fileType,
        fileSize,
      });
      router.push(`/campaigns/${campaign.id}`);
    } catch (err) {
      setError(extractErrorMessage(err, t("title")));
    } finally {
      setSubmitting(false);
    }
  }

  if (!bookingId) {
    return (
      <p className="mx-auto max-w-md px-4 py-12 text-sm text-destructive">bookingId manquant dans l&apos;URL.</p>
    );
  }

  return (
    <div className="mx-auto flex max-w-md flex-col gap-6 px-4 py-16">
      <Card>
        <CardHeader>
          <CardTitle>{t("title")}</CardTitle>
        </CardHeader>
        <CardContent>
          <form className="flex flex-col gap-4" onSubmit={handleSubmit}>
            <Input name="name" label={t("name")} value={name} onChange={(e) => setName(e.target.value)} required />
            <Input
              name="description"
              label={t("description")}
              value={description}
              onChange={(e) => setDescription(e.target.value)}
            />
            <div className="flex flex-col gap-1.5">
              <label className="text-sm font-medium" htmlFor="file">
                {t("upload")}
              </label>
              <input
                id="file"
                type="file"
                accept="image/*,video/*"
                onChange={(e) => setFile(e.target.files?.[0] ?? null)}
                className="text-sm"
              />
            </div>
            {error && <p className="text-sm text-destructive">{error}</p>}
            <Button type="submit" loading={submitting} disabled={submitting}>
              {submitting ? t("submitting") : t("submit")}
            </Button>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
