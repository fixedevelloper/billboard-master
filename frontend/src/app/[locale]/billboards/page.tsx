"use client";

import { FormEvent, useState } from "react";
import { useTranslations } from "next-intl";
import { Link } from "@/i18n/navigation";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { BillboardResponse, extractErrorMessage, searchBillboardsByCity } from "@/lib/api";

export default function BillboardsSearchPage() {
  const t = useTranslations("billboards.search");

  const [city, setCity] = useState("");
  const [searched, setSearched] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [results, setResults] = useState<BillboardResponse[]>([]);

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setLoading(true);
    setError(null);
    try {
      const billboards = await searchBillboardsByCity(city);
      setResults(billboards);
      setSearched(true);
    } catch (err) {
      setError(extractErrorMessage(err, t("title")));
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="mx-auto flex max-w-4xl flex-col gap-6 px-4 py-12">
      <h1 className="text-2xl font-semibold">{t("title")}</h1>
      <form className="flex gap-3" onSubmit={handleSubmit}>
        <Input
          name="city"
          label={t("cityLabel")}
          placeholder={t("cityPlaceholder")}
          value={city}
          onChange={(event) => setCity(event.target.value)}
          required
          className="max-w-xs"
        />
        <Button type="submit" loading={loading} disabled={loading} className="mt-6 h-fit self-start">
          {t("submit")}
        </Button>
      </form>

      {error && <p className="text-sm text-destructive">{error}</p>}

      {!searched && !error && <p className="text-sm text-muted-foreground">{t("initial")}</p>}
      {searched && results.length === 0 && !error && (
        <p className="text-sm text-muted-foreground">{t("noResults")}</p>
      )}

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 md:grid-cols-3">
        {results.map((billboard) => (
          <Card key={billboard.id}>
            <CardHeader>
              <CardTitle className="text-base">{billboard.title}</CardTitle>
            </CardHeader>
            <CardContent className="flex flex-col gap-2 text-sm">
              <p className="text-muted-foreground">{billboard.city}</p>
              <p>
                {billboard.dailyRate} {billboard.currency} <span className="text-muted-foreground">{t("dailyRate")}</span>
              </p>
              <Button asChild variant="outline" size="sm">
                <Link href={`/billboards/${billboard.id}`}>{t("viewDetails")}</Link>
              </Button>
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  );
}
