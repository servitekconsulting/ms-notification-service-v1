import os
import subprocess
from openai import OpenAI

# Inicializa cliente con la API Key
client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))

# Obtén el diff del PR
base_ref = os.getenv("GITHUB_BASE_REF")
head_ref = os.getenv("GITHUB_HEAD_REF")

subprocess.run(["git", "fetch", "origin", base_ref, head_ref])
diff = subprocess.check_output(["git", "diff", f"origin/{base_ref}...origin/{head_ref}"]).decode("utf-8")

prompt = f"Revisa el siguiente código y sugiere mejoras:\n{diff}"

# Llamada a la API con la nueva sintaxis
response = client.chat.completions.create(
    model="gpt-5-mini",  # más rápido y económico que gpt-4
    messages=[
        {"role": "system", "content": "Eres un revisor experto en código."},
        {"role": "user", "content": prompt}
    ]
)

review_comments = response.choices[0].message.content
# Publica el comentario en el PR usando GitHub CLI
subprocess.run(["gh", "pr", "comment", "--body", review_comments])
