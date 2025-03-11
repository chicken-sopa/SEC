import subprocess
import platform
import time


def start_node_in_terminal(command, name):
    """
    Inicia um nó da aplicação em uma nova janela do terminal, sem fornecer input automático.

    :param command: Comando a ser executado no terminal.
    :param name: Nome ou ID do nó para fins de logging.
    """
    print(f"Iniciando {name} em uma nova janela de terminal...")

    # Identificar o sistema operacional
    system_name = platform.system()

    if system_name == "Windows":
        # No Windows: abrir o terminal CMD com o comando e deixá-lo aberto
        subprocess.Popen(
            ["start", "cmd", "/k", " ".join(command)], shell=True
        )
    elif system_name == "Linux":
        # No Linux: abrir o terminal (gnome-terminal ou bash) e manter aberto
        bash_command = " ".join(command)
        subprocess.Popen(
            ["gnome-terminal", "--", "bash", "-c", f"{bash_command}; exec bash"]
        )
    elif system_name == "Darwin":  # macOS
        # No macOS: usar AppleScript para abrir o Terminal e executar o comando
        bash_command = " ".join(command)
        subprocess.Popen(
            ["osascript", "-e", f'tell app "Terminal" to do script "{bash_command}"']
        )
    else:
        raise Exception(
            "Sistema operacional não suportado. Apenas Windows, Linux e macOS são suportados."
        )

    # Aguardar antes de iniciar o próximo nó
    time.sleep(1)

    print(f"{name} foi iniciado com sucesso.")


# Configuração dos comandos para cada nó
nodes = [
    {
        "command": ["java", "-cp", "Server/target/classes", "Main"],
        "name": "Node-2",
    },
    {
        "command": ["java", "-cp", "Server/target/classes", "Main"],
        "name": "Node-1",
    },
    {
        "command": ["java", "-cp", "Server/target/classes", "Main"],
        "name": "Node-0",
    },
]

# Compilar o projeto com Maven (opcional)
#print("Compilando o projeto...")
#subprocess.run(["mvn", "clean", "install"], check=True, cwd="Server")
#print("Compilação concluída.")

# Iniciar todos os nós nos terminais
for node_info in nodes:
    start_node_in_terminal(
        command=node_info["command"],
        name=node_info["name"],
    )

print("Todos os nós foram iniciados em terminais separados.")
